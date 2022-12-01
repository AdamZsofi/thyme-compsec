export async function checkLoginStatus() {
    const res = await fetch('https://localhost:8080/user/ami_logged_in', {
        credentials: 'include',
        method: "GET",
        mode: 'cors',
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
    const res = await fetch('https://localhost:8080/user/ami_admin', {
        credentials: 'include',
        method: "GET",
        mode: 'cors',
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

export async function getCaffs() {
    const res = await fetch(`/api/caff`, {
        method: "GET",
    })
    if (res.ok) {
        const json = (await res.json());
        var caffArr = [];
        for(var i in json.caffs) {
        caffArr.push(json.caffs[i]);
        }
        return caffArr;
    } else {
        // TODO this works?
        return [];
    }
}

export async function getSearchResult() {
    // TODO
    return [];
}

export async function postUserLogin(username, password, router) {
  const res = await fetch(`https://localhost:8080/user/login?` + new URLSearchParams({username: username, password: password}), {
      method: "POST",
      mode: 'no-cors',
    })
    if (res.ok) {
      console.log(res);
      router.push("/");
    } else {
      if(res.status==401) {
        alert("Bad credentials");
      } else if (res.status==500) {
        alert("Server error");
      } else {
        alert("Unknown Error")
      }
    } 
}

export async function postUserRegistration(username, password, router) {
  if(state.errorMessage==='Password OK') {
      const res = await fetch(`/user/register?` + new URLSearchParams({username: username, password: password}), {
        method: "POST",
      })
      if (res.ok) {
        console.log(res)
        router.push("/signin")
      } else {
        alert("Could not sign you up, please try again!")
      }
    } else {
      alert("Password is not strong enough!");
    }  
}

export async function getCaff(id) {
    if(id === undefined) {
      return undefined;
    }
    const res = await fetch(`/api/caff/`+id, {
      method: "GET",
    })
    if (res.ok) {
      const json = (await res.json());
      return json;
    } else {
      // TODO this works?
      return undefined;
    }
}
  
export async function getComments(id) {
    if(id === undefined) {
      console.log("undefined");
      return [];
    }
  
    const res = await fetch(`/api/caff/comment/`+id, {
      method: "GET",
    })
    if (res.ok) {
      const json = (await res.json());
      var comments = [];
      for(var i in json.comments) {
        comments.push(json.comments[i]);
      }
      return comments;
    } else {
      // TODO this works?
      return [];
    }
}
  