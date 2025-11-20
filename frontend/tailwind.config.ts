/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      backgroundImage: {
        space: "url('/src/assets/bg-space.jpg')",
      },
    },
  },
  safelist: [
    "bg-pink-500",
    "ring-pink-300",
    "animate-bounce",
    "bg-lime-400",
    "ring-lime-500",
    "bg-gray-800",
    "hover:bg-gray-700",
    "text-white",
  ],
  plugins: [],
};
