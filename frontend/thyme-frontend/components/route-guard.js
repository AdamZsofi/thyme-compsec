import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import axios from 'axios';

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

    function checkLoginStatus() {
        axios.get("/ami_logged_in", { withCredentials: true }).then(response => {
                console.log(response.data);
                if(typeof response.data === "undefined") {
                    return false;
                }
                return response.data;
            }
        ).catch(error => {
            console.log(error);
        })
    }

    function authCheck(url) {
        // redirect to login page if accessing a private page and not logged in 
        const publicPaths = ['/signin'];
        const path = url.split('?')[0];
        
        console.log(document.cookie);
        const cookieValue = document.cookie.split('; ').filter(row => row.startsWith('JSESSIONID=')).map(c=>c.split('=')[1])[0];
        console.log(cookieValue);

        checkLoginStatus();

        if (checkLoginStatus() && !publicPaths.includes(path)) {
            setAuthorized(false);
            router.push({
                pathname: '/signin'
            });    
        } else {
            setAuthorized(true);
        }
    }

    return (authorized && children);
}

export default RouteGuard;