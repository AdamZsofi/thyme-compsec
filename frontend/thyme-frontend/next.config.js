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
    {
      source: "/api/caff/:path",
      destination: "http://localhost:8080/api/caff/:path"
    },
    {
      source: "/api/caff/comment/:path",
      destination: "http://localhost:8080/api/caff/comment/:path"
    },
    {
      source: "/ami_logged_in",
      destination: "http://localhost:8080/ami_logged_in"
    },
  ]
}

module.exports = nextConfig
