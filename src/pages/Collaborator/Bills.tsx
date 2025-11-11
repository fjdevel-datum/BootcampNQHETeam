import React, { useState, useEffect, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Camera, Paperclip, X, Check, RotateCcw, ArrowLeft, Loader2, FileText } from "lucide-react";
import { toast } from "../../components/toast";
import BillCard from "../../components/BillCard";
import { fetchWithAuth } from "../../services/authService";

interface Expense {
  gastoId: number;
  descripcionGasto: string;
  totalGasto: number;
  fecha: string;
}

interface GeminiData {
  Nombre_Pagador: string;
  Fecha: string;
  Monto_Total: number;
  Numero_Tarjeta: string;
  Descripcion_Item: string;
  Cantidad_Item: number;
}

interface OCRResponse {
  draftId: string;
  geminiData: GeminiData;
}

interface FacturaResponse {
  facturaId: number;
}

const Bills: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [loadingExpenses, setLoadingExpenses] = useState(true);
  const [capturedPhoto, setCapturedPhoto] = useState<string | null>(null);
  const [fileType, setFileType] = useState<'image' | 'pdf' | null>(null);
  const [showApproval, setShowApproval] = useState(false);
  const [loading, setLoading] = useState(false);

  const cameraInputRef = useRef<HTMLInputElement | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [showCamera, setShowCamera] = useState(false);
  const [stream, setStream] = useState<MediaStream | null>(null);

  const apiUrl = import.meta.env.VITE_API_URL || "http://localhost:8080";

  useEffect(() => {
    const loadExpenses = async () => {
      if (!id) {
        console.error("❌ No se proporcionó ID de actividad");
        setLoadingExpenses(false);
        return;
      }

      try {
        setLoadingExpenses(true);
        console.log(`🔍 Cargando gastos para actividad ID: ${id}`);

        const data = await fetchWithAuth(`${apiUrl}/gastos/actividad/${id}`);
        
        console.log("✅ Gastos obtenidos:", data);
        
        setExpenses(data.map((gasto: any) => ({
          gastoId: gasto.gastoId,
          descripcionGasto: gasto.descripcionGasto,
          totalGasto: gasto.totalGasto,
          fecha: gasto.fecha
        })));

        if (data.length > 0) {
          toast.success("Gastos cargados", `${data.length} gasto${data.length !== 1 ? 's' : ''} encontrado${data.length !== 1 ? 's' : ''}`);
        }
      } catch (err: any) {
        console.error("❌ Error al cargar gastos:", err);
        toast.error("Error", "No se pudieron cargar los gastos de esta actividad");
        setExpenses([]);
      } finally {
        setLoadingExpenses(false);
      }
    };

    loadExpenses();
  }, [id, apiUrl]);

  useEffect(() => {
    return () => {
      if (stream) stream.getTracks().forEach((track) => track.stop());
    };
  }, [stream]);

  const handleGoBack = () => navigate(-1);

  const handleAddPhoto = () => {
    if (/Mobi|Android/i.test(navigator.userAgent)) {
      cameraInputRef.current?.click();
    } else {
      setShowCamera(true);
      startCamera();
    }
  };

  const handleAddFile = () => fileInputRef.current?.click();

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      const isPDF = file.type === 'application/pdf';
      
      console.log("📄 Archivo seleccionado:", file.name, file.type);
      
      setFileType(isPDF ? 'pdf' : 'image');
      
      const reader = new FileReader();
      reader.onloadend = () => {
        setCapturedPhoto(reader.result as string);
        setShowApproval(true);
        toast.success(
          isPDF ? "PDF cargado" : "Archivo cargado", 
          "Revisa el archivo antes de aprobar"
        );
      };
      reader.readAsDataURL(file);
    }
  };

  const startCamera = async () => {
    try {
      const mediaStream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: "environment" },
      });
      setStream(mediaStream);
      if (videoRef.current) videoRef.current.srcObject = mediaStream;
      toast.success("Cámara activada", "Posiciona el comprobante y toma la foto");
    } catch (err) {
      console.error(err);
      toast.error("Error de cámara", "No se pudo acceder a la cámara del dispositivo");
    }
  };

  const handleTakePhoto = () => {
    if (videoRef.current && canvasRef.current) {
      const ctx = canvasRef.current.getContext("2d");
      if (ctx) {
        const video = videoRef.current;
        canvasRef.current.width = video.videoWidth;
        canvasRef.current.height = video.videoHeight;
        ctx.drawImage(video, 0, 0, video.videoWidth, video.videoHeight);
        const imageData = canvasRef.current.toDataURL("image/jpeg", 0.8);
        setCapturedPhoto(imageData);
        setFileType('image');
        setShowApproval(true);
        setShowCamera(false);
        toast.success("Foto capturada", "Revisa la imagen antes de aprobar");
      }
    }
  };

  const closeCamera = () => {
    if (stream) stream.getTracks().forEach((track) => track.stop());
    if (videoRef.current) videoRef.current.srcObject = null;
    setStream(null);
    setShowCamera(false);
  };

  const handleApprove = async () => {
  if (!capturedPhoto) {
    toast.error("Error", "No hay archivo capturado");
    return;
  }

  setLoading(true);
  let loadingToastId: any = null;

  try {
    console.log("🎬 INICIO handleApprove");

    loadingToastId = toast.loading("Procesando imagen...");

    const blob = await fetch(capturedPhoto).then((r) => r.blob());
    if (blob.size === 0) throw new Error("La imagen está vacía");

    const token = localStorage.getItem("token");
    const headers: HeadersInit = token
      ? { Authorization: `Bearer ${token}` }
      : {};

    const formDataFactura = new FormData();
    formDataFactura.append("file", blob, "comprobante.jpg");

    const formDataOCR = new FormData();
    formDataOCR.append("file", blob, "comprobante.jpg");

    console.log("🚀 Enviando solicitudes paralelas...");

    // ⚙️ Ejecutar OCR y Factura en paralelo (aunque una falle)
    const [facturaResponse, ocrResponse] = await Promise.allSettled([
      fetch(`${apiUrl}/factura/upload`, {
        method: "POST",
        headers,
        body: formDataFactura,
      }),
      fetch(`${apiUrl}/ocr2/extract2`, {
        method: "POST",
        headers,
        body: formDataOCR,
      }),
    ]);

    let facturaId: number | null = null;
    let ocrData: OCRResponse | null = null;

    // 🧾 Procesar factura (sin bloquear)
    if (
      facturaResponse.status === "fulfilled" &&
      facturaResponse.value.ok
    ) {
      const facturaJson: FacturaResponse = await facturaResponse.value.json();
      facturaId = facturaJson.facturaId;
      console.log("✅ Factura subida con éxito:", facturaId);
    } else {
      console.warn("⚠️ Error al subir factura (continuando flujo)");
      toast.warning("Advertencia", "No se pudo subir la factura, se continuará el proceso.");
    }

    // 🔍 Procesar OCR
    if (
      ocrResponse.status === "fulfilled" &&
      ocrResponse.value.ok
    ) {
      ocrData = await ocrResponse.value.json();
      console.log("✅ OCR procesado correctamente");
    } else {
      const errText =
        ocrResponse.status === "fulfilled"
          ? await ocrResponse.value.text()
          : "No se pudo conectar con el OCR";
      throw new Error(`Error en OCR: ${errText}`);
    }

    if (!ocrData?.draftId || !ocrData?.geminiData) {
      throw new Error("Respuesta OCR incompleta");
    }

    // 💾 Guardar en sessionStorage
    const dataToSave = {
      draftId: ocrData.draftId,
      geminiData: ocrData.geminiData,
      imageUrl: capturedPhoto,
      timestamp: Date.now(),
      actividadId: id,
      facturaId: facturaId, // puede ser null
    };
    sessionStorage.setItem("ocrData", JSON.stringify(dataToSave));

    if (loadingToastId) toast.dismiss(loadingToastId);
    toast.success("Proceso completado", "Factura y OCR procesados correctamente");

    console.log("➡️ Navegando a NewBill...");

    navigate("/NewBill", {
      state: {
        actividadId: id,
        facturaId: facturaId,
      },
    });
  } catch (err) {
    console.error("❌ ERROR CAPTURADO:", err);
    if (loadingToastId) toast.dismiss(loadingToastId);
    toast.error(
      "Error en proceso",
      err instanceof Error ? err.message : "No se pudo procesar la imagen"
    );
  } finally {
    setLoading(false);
    closeCamera();
    setShowApproval(false);
    setCapturedPhoto(null);
    setFileType(null);
  }
};


  const handleRetake = () => {
    toast.info("Repitiendo captura", "Toma una nueva fotografía o selecciona otro archivo");
    setCapturedPhoto(null);
    setFileType(null);
    setShowApproval(false);
    if (!/Mobi|Android/i.test(navigator.userAgent)) {
      setShowCamera(true);
      if (!stream) startCamera();
    }
  };

  const handleCancel = () => {
    toast.warning("Archivo descartado", "El archivo no fue guardado");
    setCapturedPhoto(null);
    setFileType(null);
    setShowApproval(false);
    closeCamera();
  };

  return (
    <div className="min-h-screen bg-background font-montserrat relative">
      <header className="relative flex items-center p-6 bg-background">
        <button
          onClick={handleGoBack}
          className="absolute left-4 flex items-center gap-2 text-black hover:text-activity transition"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
      </header>

      <main className="px-4 py-6 md:px-8 max-w-3xl mx-auto">
        <h2 className="text-base md:text-lg font-semibold text-black mb-4">
          Gastos Registrados:
        </h2>

        {loadingExpenses ? (
          <div className="flex justify-center items-center py-8">
            <Loader2 className="w-8 h-8 text-button animate-spin" />
            <span className="ml-3 text-gray-600">Cargando gastos...</span>
          </div>
        ) : expenses.length === 0 ? (
          <div className="text-center py-8">
            <p className="text-gray-500">No hay gastos registrados en esta actividad</p>
            <p className="text-gray-400 text-sm mt-2">Agrega tu primer gasto usando los botones de abajo</p>
          </div>
        ) : (
          <div className="space-y-3">
            {expenses.map((exp) => (
              <BillCard 
                key={exp.gastoId} 
                title={exp.descripcionGasto} 
                date={exp.fecha} 
                amount={exp.totalGasto} 
              />
            ))}
          </div>
        )}

        {!showCamera && !showApproval && (
          <div className="flex justify-center gap-6 mt-10">
            <button
              onClick={handleAddPhoto}
              className="w-14 h-14 rounded-full bg-button hover:bg-button-hover flex items-center justify-center shadow-lg transition-colors"
              title="Tomar foto"
            >
              <Camera className="text-white w-6 h-6" />
            </button>

            <button
              onClick={handleAddFile}
              className="w-14 h-14 rounded-full bg-button hover:bg-button-hover flex items-center justify-center shadow-lg transition-colors"
              title="Adjuntar archivo"
            >
              <Paperclip className="text-white w-6 h-6" />
            </button>
          </div>
        )}

        <input
          type="file"
          accept="image/*"
          capture="environment"
          ref={cameraInputRef}
          className="hidden"
          onChange={handleFileChange}
        />
        <input
          type="file"
          accept="image/*,.pdf"
          ref={fileInputRef}
          className="hidden"
          onChange={handleFileChange}
        />

        {showCamera && (
          <div className="fixed inset-0 bg-black bg-opacity-90 flex flex-col items-center justify-center z-50 p-4">
            <div className="relative max-w-md w-full">
              <button
                onClick={closeCamera}
                className="absolute top-4 right-4 z-10 w-10 h-10 rounded-full bg-red-600 hover:bg-red-700 flex items-center justify-center"
              >
                <X className="text-white w-5 h-5" />
              </button>

              <video ref={videoRef} autoPlay playsInline className="w-full rounded-lg shadow-lg" />

              <button
                onClick={handleTakePhoto}
                className="mt-6 w-full py-3 bg-button hover:bg-button-hover text-white rounded-lg font-semibold transition-colors flex items-center justify-center gap-2"
              >
                <Camera className="w-5 h-5" /> Tomar Foto
              </button>
            </div>
            <canvas ref={canvasRef} className="hidden" />
          </div>
        )}

        {showApproval && capturedPhoto && (
          <div className="fixed inset-0 bg-black bg-opacity-90 flex flex-col items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
              <h3 className="text-lg font-semibold text-black mb-4 text-center">
                Vista Previa
              </h3>

              {fileType === 'pdf' ? (
                <div className="w-full rounded-lg shadow-md bg-gray-100 mb-6 p-8 flex flex-col items-center justify-center min-h-64">
                  <FileText className="w-16 h-16 text-red-600 mb-4" />
                  <p className="text-gray-700 font-semibold">Archivo PDF seleccionado</p>
                  <p className="text-gray-500 text-sm mt-2">El PDF será procesado correctamente</p>
                </div>
              ) : (
                <img
                  src={capturedPhoto}
                  alt="Vista previa"
                  className="w-full rounded-lg shadow-md max-h-64 object-contain bg-gray-100 mb-6"
                />
              )}

              <div className="flex gap-3">
                <button
                  onClick={handleCancel}
                  disabled={loading}
                  className="flex-1 py-3 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg font-semibold transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
                >
                  <X className="w-4 h-4" /> Cancelar
                </button>
                <button
                  onClick={handleRetake}
                  disabled={loading}
                  className="flex-1 py-3 bg-button hover:bg-button-hover text-white rounded-lg font-semibold transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
                >
                  <RotateCcw className="w-4 h-4" /> Repetir
                </button>
                <button
                  onClick={handleApprove}
                  disabled={loading}
                  className="flex-1 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg font-semibold transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
                >
                  {loading ? (
                    <>
                      <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      Procesando...
                    </>
                  ) : (
                    <>
                      <Check className="w-4 h-4" /> Aprobar
                    </>
                  )}
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default Bills;