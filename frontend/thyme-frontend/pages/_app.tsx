import '../styles/globals.css'
import RouteGuard from '../components/route-guard.js'
import type { AppProps } from 'next/app'

export default function App({ Component, pageProps }: AppProps) {    
  return (
    <RouteGuard>
      <Component {...pageProps} />
    </RouteGuard>
  );
}
