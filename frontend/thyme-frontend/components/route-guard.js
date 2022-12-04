import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { checkLoginStatus, checkAdminLoginStatus } from './rest-api-calls.js';

// https://jasonwatmore.com/post/2021/08/30/next-js-redirect-to-login-page-if-unauthenticated
function RouteGuard({ children }) {
    const router = useRouter();
    const [authorized, setAuthorized] = useState(false);

    useEffect(() => {
        // on initial load - run auth check 
        authCheck(router.asPath);

        // on route change start - hide page content by setting authorized to false  
        const hideContent = () => setAuthorized(false);
        router.events.on('routeChangeStart', hideContent);
        
        // on route change complete - run auth check 
        router.events.on('routeChangeComplete', authCheck)

        // unsubscribe from events in useEffect return function
        return () => {
            router.events.off('routeChangeStart', hideContent);
            router.events.off('routeChangeComplete', authCheck);
        }

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    async function authCheck(url) {
        // redirect to login page if accessing a private page and not logged in 
        const adminOnlyPath = ['/admin'];
        const publicPaths = ['/signin', '/signup']; // public as in user shall NOT be logged in to access
        const path = url.split('?')[0];

        const isLoggedIn = await checkLoginStatus();
        const isAdmin = await checkAdminLoginStatus();

        if(adminOnlyPath.includes(path)) {
            if(isAdmin) {
                setAuthorized(true);
            } else if(isLoggedIn) {
                setAuthorized(false);
                router.push({
                    pathname: '/'
                });    
            } else {
                setAuthorized(false);
                router.push({
                    pathname: '/signin'
                });
            }
        } else if(isLoggedIn) {
            if(publicPaths.includes(path)) {
                setAuthorized(false);
                router.push({
                    pathname: '/logout'
                });
            } else {
                setAuthorized(true);
            }
        } else {
            if(publicPaths.includes(path)) {
                setAuthorized(true);
            } else {
                setAuthorized(false);
                router.push({
                    pathname: '/signin'
                });
            }
        }
    }

    return (authorized && children);
}

export default RouteGuard;