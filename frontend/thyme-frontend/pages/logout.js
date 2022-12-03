import React from 'react';
import { useRouter } from 'next/router';
import { logout } from '../components/rest-api-calls.js'

function handleLogout(router) {
    logout(router);
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