/** @type {import('next').NextConfig} */
// const server_address = 'https://localhost:8080'

const nextConfig = {
  reactStrictMode: false,
  swcMinify: true,
  /*
  rewrites: async () => [
    {
      source: "/user/login",
      destination: server_address+"/user/login"
    },
    {
      source: "/csrf",
      destination: server_address+"/csrf"
    },
    {
      source: "/api/caff",
      destination: server_address+"/api/caff"
    },
    {
      source: "/api/caff/:path",
      destination: server_address+"/api/caff/:path"
    },
    {
      source: "/api/caff/comment/:path",
      destination: server_address+"/api/caff/comment/:path"
    },
    {
      source: "/user/ami_logged_in",
      destination: server_address+"/user/ami_logged_in"
    },
    {
      source: "/user/ami_admin",
      destination: server_address+"/user/ami_admin"
    },
  ]*/
}

module.exports = nextConfig
