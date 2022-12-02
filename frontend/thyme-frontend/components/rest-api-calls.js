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
        return [];
    }
}

export async function getSearchResult(key) {
  const res = await fetch(server_address + '/api/caff/search/'+key, {
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
      return [];
  }
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
      return undefined;
    }
}

export async function getPreview(id) {
    return server_address + '/api/caff/preview/' + id
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
      return [];
    }
}

export async function uploadCaffForm(formData, router) {
  const csrf = await getCsrfToken();
  
  const res = await fetch(server_address + '/api/upload', {
    method: "POST",
    mode: 'cors',
    credentials: 'include',
    body: formData,
    headers: {
      "X-CSRF-TOKEN": csrf.token
    },
  })
  if (res.ok) {
    alert("Successful upload");
    router.push("/");
  } else {
    alert("Upload unsuccessful, try again!")
  }
} 

async function getCsrfToken() {
  const res = await fetch(server_address + '/csrf', {
    method: "GET",
    mode: 'cors',
    credentials: 'include'    
  })
  if (res.ok) {
      const json = await res.json();
      return json;
  } else {
      // TODO some kind of error instead?
      return undefined;
  }
}

export async function uploadComment(comment, caffId) {
  const formData = new FormData();

  formData.append("comment", comment);
  formData.append("file_id", caffId);
  
  const csrf = await getCsrfToken();
  
  const res = await fetch(server_address + '/api/comment', {
    method: "POST",
    mode: 'cors',
    credentials: 'include',
    body: formData,
    headers: {
      "X-CSRF-TOKEN": csrf.token
    },
  })
  if (res.ok) {
    alert("Successfully submitted comment");
    router.push("/");
  } else {
    alert("Comment submission unsuccessful, try again!")
  }
}

export async function buyCaff(caffId) {
  const formData = new FormData();

  formData.append("id", caffId);
  
  const csrf = await getCsrfToken();
  
  const res = await fetch(server_address + '/api/buy', {
    method: "POST",
    mode: 'cors',
    credentials: 'include',
    body: formData,
    headers: {
      "X-CSRF-TOKEN": csrf.token
    },
  })
  if (res.ok) {
    alert("Successfully bought caff, now the website will download it");
    downloadCaff(caffId);
  } else {
    alert("Could not buy caff, try again!")
  }
}

async function downloadCaff(id) {
  const csrf = await getCsrfToken();
  
  const res = await fetch(server_address + '/api/download/'+id, {
    method: "GET",
    mode: 'cors',
    credentials: 'include',
    headers: {
    "X-CSRF-TOKEN": csrf.token
    },
  })
  if (res.ok) {
    const blob = await res.blob();
    var a = document.createElement("a");
    a.href = window.URL.createObjectURL(blob);
    a.download = id+".caff";
    a.click();
  } else {
    alert("Could not download caff, try to buy it again!")
  }
}
