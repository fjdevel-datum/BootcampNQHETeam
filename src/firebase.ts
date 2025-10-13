// src/firebase.ts
// Import the functions you need from the SDKs you need
 import { initializeApp } from "firebase/app";
 import { getAuth } from "firebase/auth";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyC6dwhGTvn4yfAaQJbfPs1GcNWDNNpGzV4",
  authDomain: "easycheck-9441b.firebaseapp.com",
  projectId: "easycheck-9441b",
  storageBucket: "easycheck-9441b.firebasestorage.app",
  messagingSenderId: "353440926067",
  appId: "1:353440926067:web:cc5eb4bae9df4bf597dd06",
  measurementId: "G-F3WDH8MB0P"
};

// Initialize Firebase
 const app = initializeApp(firebaseConfig);
 export const auth = getAuth(app);