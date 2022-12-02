const server_address = 'https://localhost:8080';

export async function checkLoginStatus() {
    const res = await fetch(server_address + '/user/ami_logged_in', {
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
    const res = await fetch(server_address + '/user/ami_admin', {
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
    const res = await fetch(server_address + '/api/caff', {
        method: "GET",
        mode: 'cors',
        credentials: 'include'    
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
  const formData = new FormData();

  formData.append("username", username);
  formData.append("password", password);
  
  const res = await fetch(server_address + '/user/login', {
    method: "POST",
    mode: 'cors',
    credentials: 'include',
    body: formData,
  })
  for(const key of res.headers.keys()) {
    console.log(key);
    console.log("Value:");
    console.log(res.headers.get(key));
  }
  console.log(await res.text());
  if (res.ok) {
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
  const formData = new FormData();

  formData.append("username", username);
  formData.append("password", password);

  if(state.errorMessage==='Password OK') {
      const res = await fetch(server_address + '/user/register', {
        method: "POST",
        mode: 'cors',
        credentials: 'include',
        body: formData,
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
    const res = await fetch(server_address + '/api/caff/'+id, {
      method: "GET",
      mode: 'cors',
      credentials: 'include'
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
  
    const res = await fetch(server_address + '/api/caff/comment/'+id, {
      method: "GET",
      mode: 'cors',
      credentials: 'include'
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
  