import React from 'react';
import { useRouter } from 'next/router';

function handleLogout(router) {
    //TODO server logout
    console.log("TODO server logout");
    //router.push({
    //    pathname: '/signin'
    //});
}

function LogoutPage(props) {
    const router= useRouter();
    
    return (
        <div>
            <button onClick={() => handleLogout(router)}>Click here to log out</button>
        </div>
    )
}

export default LogoutPage