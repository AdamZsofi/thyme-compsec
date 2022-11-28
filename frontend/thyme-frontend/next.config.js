/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  rewrites: async () => [
    {
      source: "/login",
      destination: "http://localhost:8080/login"
    },
    {
      source: "/csrf",
      destination: "http://localhost:8080/csrf"
    },
    {
      source: "/api/caff",
      destination: "http://localhost:8080/api/caff"
    },
  ]
}

module.exports = nextConfig
