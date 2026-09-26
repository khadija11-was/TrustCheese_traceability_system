/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,jsx}",
    ],
    theme: {
        extend: {
            colors: {
                brand: {
                    primary: '#1E3A8A',   // Bleu industriel
                    success: '#16A34A',   // Vert pour lots libérés
                    danger: '#DC2626',    // Rouge pour lots bloqués
                    warning: '#D97706',   // Orange pour lots en attente QC
                }
            }
        },
    },
    plugins: [],
}