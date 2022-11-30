import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';

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

        if(adminOnlyPath.includes(path)) {
            if(await checkAdminLoginStatus()) {
                setAuthorized(true);
            } else {
                setAuthorized(false);
                router.push({
                    pathname: '/'
                });    
            }
        } else if(await checkLoginStatus()) {
            console.log("HEY");
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

async function checkLoginStatus() {
    const res = await fetch('/user/ami_logged_in', {
        credentials: 'include',
        method: "GET",
    })
    if (res.ok) {
        console.log(res)
        if(typeof res.data === 'undefined' || res.data === 'undefined') {
            return false;
        }
        return res.data;
    } else {
        return false;
    }
}

async function checkAdminLoginStatus() {
    const res = await fetch('/user/ami_admin', {
        credentials: 'include',
        method: "GET",
    })
    if (res.ok) {
        console.log(res)
        if(typeof res.data === 'undefined' || res.data === 'undefined') {
            return false;
        }
        return res.data;
    } else {
        return false;
    }
}

export default RouteGuard;