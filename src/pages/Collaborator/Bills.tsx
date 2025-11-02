import React, { useState, useEffect, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Camera, Paperclip, X, Check, RotateCcw, ArrowLeft } from "lucide-react";
import { toast } from "../../components/toast";
import BillCard from "../../components/BillCard";

interface Expense {
  id: number;
  title: string;
  amount: number;
  date: string;
  photo?: string;
}

const Bills: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [capturedPhoto, setCapturedPhoto] = useState<string | null>(null);
  const [showApproval, setShowApproval] = useState(false);

  // refs
  const cameraInputRef = useRef<HTMLInputElement | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  const [showCamera, setShowCamera] = useState(false);
  const [stream, setStream] = useState<MediaStream | null>(null);

  //const apiurl = import.meta.env.VITE_API_URL;

  // gastos de prueba
  useEffect(() => {
    const mockExpenses: Expense[] = [
      { id: 1, title: "Taxi al cliente", amount: 25.5, date: "2025-01-18" },
      { id: 2, title: "Almuerzo con socio", amount: 40.0, date: "2025-01-18" },
    ];
    setExpenses(mockExpenses);
  }, [id]);

  // detener cámara al salir
  useEffect(() => {
    return () => {
      if (stream) {
        stream.getTracks().forEach((track) => track.stop());
      }
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
      const url = URL.createObjectURL(file);
      setCapturedPhoto(url);
      setShowApproval(true);
      toast.success("Archivo cargado", "Revisa la imagen antes de aprobar");
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
        setCapturedPhoto(canvasRef.current.toDataURL("image/jpeg", 0.8));
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

  const handleApprove = () => {
    toast.success("Foto aprobada", "Completa los datos del gasto");
    navigate("/BillCheck", {
      state: { photo: capturedPhoto }
    });
    setCapturedPhoto(null);
    setShowApproval(false);
    closeCamera();
  };

  const handleRetake = () => {
    toast.info("Repitiendo foto", "Toma una nueva fotografía");
    setCapturedPhoto(null);
    setShowApproval(false);
    if (!/Mobi|Android/i.test(navigator.userAgent)) {
      setShowCamera(true);
      if (!stream) startCamera();
    }
  };

  const handleCancel = () => {
    toast.warning("Foto descartada", "La imagen no fue guardada");
    setCapturedPhoto(null);
    setShowApproval(false);
    closeCamera();
    if (capturedPhoto?.startsWith("blob:")) URL.revokeObjectURL(capturedPhoto);
  };

  return (
    <div className="min-h-screen bg-background font-monserrat relative">
      {/* Header */}
      <header className="relative flex items-center p-6 bg-background">
        <button
          onClick={handleGoBack}
          className="absolute left-4 flex items-center gap-2 text-black hover:text-activity transition"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
      </header>

      {/* Body */}
      <main className="px-4 py-6 md:px-8 max-w-3xl mx-auto">
        <h2 className="text-base md:text-lg font-semibold text-black mb-4">
          Gastos Registrados:
        </h2>

        {/* Lista de gastos */}
        <div className="space-y-3">
          {expenses.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-500">No hay gastos registrados</p>
              <p className="text-gray-400 text-sm mt-2">
                Usa los botones de abajo para agregar un nuevo gasto
              </p>
            </div>
          ) : (
            expenses.map((exp) => (
              <BillCard
                key={exp.id}
                title={exp.title}
                date={exp.date}
                amount={exp.amount}
              />
            ))
          )}
        </div>

        {/* Botones flotantes */}
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

        {/* Inputs ocultos */}
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
          accept="image/*,.pdf,.jpg,.png"
          ref={fileInputRef}
          className="hidden"
          onChange={handleFileChange}
        />

        {/* Modal cámara */}
        {showCamera && (
          <div className="fixed inset-0 bg-black bg-opacity-90 flex flex-col items-center justify-center z-50 p-4">
            <div className="relative max-w-md w-full">
              <button
                onClick={closeCamera}
                className="absolute top-4 right-4 z-10 w-10 h-10 rounded-full bg-red-600 hover:bg-red-700 flex items-center justify-center"
              >
                <X className="text-white w-5 h-5" />
              </button>

              <video
                ref={videoRef}
                autoPlay
                playsInline
                className="w-full rounded-lg shadow-lg"
              />

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

        {/* Modal aprobación */}
        {showApproval && capturedPhoto && (
          <div className="fixed inset-0 bg-black bg-opacity-90 flex flex-col items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
              <h3 className="text-lg font-semibold text-black mb-4 text-center">
                Vista Previa
              </h3>

              <div className="mb-6">
                <img
                  src={capturedPhoto}
                  alt="Vista previa"
                  className="w-full rounded-lg shadow-md max-h-64 object-contain bg-gray-100"
                />
              </div>

              <div className="flex gap-3">
                <button
                  onClick={handleCancel}
                  className="flex-1 py-3 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg font-semibold transition-colors flex items-center justify-center gap-2"
                >
                  <X className="w-4 h-4" /> Cancelar
                </button>

                <button
                  onClick={handleRetake}
                  className="flex-1 py-3 bg-button hover:bg-button-hover text-white rounded-lg font-semibold transition-colors flex items-center justify-center gap-2"
                >
                  <RotateCcw className="w-4 h-4" /> Repetir
                </button>

                <button
                  onClick={handleApprove}
                  className="flex-1 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg font-semibold transition-colors flex items-center justify-center gap-2"
                >
                  <Check className="w-4 h-4" /> Aprobar
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