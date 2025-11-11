/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        'sans': ['Montserrat', 'ui-sans-serif', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'Helvetica Neue', 'Arial', 'Noto Sans', 'sans-serif'],
        'montserrat': ['Montserrat', 'sans-serif'],
      },
      colors: {
        'background': '#F0F1F2',
        'button': '#F23030',
        'button-hover': '#F23030',
        'black': '#212529',
        'activity': '#56828C',
      },
      backgroundColor: {
        'default': '#F0F1F2',
      }
    },
  },
  plugins: [],
}