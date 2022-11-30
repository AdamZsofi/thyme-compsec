/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: false,
  swcMinify: true,
  rewrites: async () => [
    {
      source: "/user/login",
      destination: "http://localhost:8080/user/login"
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
      source: "/user/ami_logged_in",
      destination: "http://localhost:8080/user/ami_logged_in"
    },
    {
      source: "/user/ami_admin",
      destination: "http://localhost:8080/user/ami_admin"
    },
  ]
}

module.exports = nextConfig
