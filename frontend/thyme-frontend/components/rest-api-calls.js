export async function checkLoginStatus() {
    const res = await fetch('/user/ami_logged_in', {
        credentials: 'include',
        method: "GET",
    })
    if (res.ok) {
        const answer = await res.text();
        if(typeof answer === 'undefined' || answer === 'undefined') {
            return false;
        }

        return (answer === 'true');
    } else {
        return false;
    }
}

export async function checkAdminLoginStatus() {
    const res = await fetch('/user/ami_admin', {
        credentials: 'include',
        method: "GET",
    })
    if (res.ok) {
        const answer = await res.text();
        if(typeof answer === 'undefined' || answer === 'undefined') {
            return false;
        }

        return (answer === 'true');
    } else {
        return false;
    }
}
