import React from 'react';
import ThymeHeader from '../../components/header.js';
import {CaffFile} from '../../components/caff-data.js';
import { withRouter } from 'next/router'
import Link from 'next/link.js';

import { checkAdminLoginStatus } from '../../components/rest-api-calls.js';

// TODO put rest api calls into separate file in components?
async function getCaff(id) {
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

async function getComments(id) {
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

////////////////////////////////////////////////////////////////////

class CaffInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = { 
      basicCaff: {},
      isAdmin: false
    }
  }

  async fetchState() {
    this.setState({isAdmin: checkAdminLoginStatus()});

    const caff = await getCaff(this.props.caffId);
    this.setState({basicCaff: caff});
  }

  async componentDidUpdate(prevProps) {
    if (this.props.caffId !== prevProps.caffId) {
      this.fetchState();
    }
  }

  async componentDidMount() {
    this.fetchState();
  }

  onDelete() {
    alert("TODO delete caff through server")
    this.props.router.push({
      pathname: '/'
    });
  }

  render() {
    if(typeof this.state.basicCaff === "undefined") {
      return (
        <div>
          <h1>CAFF File could not be found</h1>
          <Link href="/">Go back to the home page</Link>
        </div>
      );
    }

    return (
      <div className="caffInfo">        
        <h1>{this.state.basicCaff.filename}</h1>
        <h2>Added by {this.state.basicCaff.username}</h2>
        <p><b>Tags:</b> {this.props.tags.join(", ")}</p>
        <img className="caffImage" src={this.props.previewUrl} alt="preview of caff"/>
        <br />
        <button onClick={this.props.onClick}>Buy CAFF</button>
        {
          this.state.isAdmin ? <div><hr /><button onClick={this.onDelete.bind(this)}>Delete CAFF</button></div> : <></>
        }
      </div>
    );
  }
}

function Comment(props) {
  return (
    <div>
      <h3>{props.userName} said:</h3>
      <p>{props.comment}</p>
      <hr></hr>
    </div>
  );
}

class CommentList extends React.Component {
  constructor(props) {
    super(props);
    this.state = { commentList: []}
  }

  async fetchState() {
    const commentList = await getComments(this.props.caffId);
    this.setState({commentList: commentList});
  }

  async componentDidUpdate(prevProps) {
    if (this.props.caffId !== prevProps.caffId) {
      this.fetchState();
    }
  }

  async componentDidMount() {
    this.fetchState();
  }

  render() {
    const comments = this.state.commentList.map((c) => {
      return(
        <div key={c.id}>
          <Comment 
            userName={c.author}
            comment={c.content}
          />
        </div>
      );
    });

    return (
      <div>
        <h1>Comments about this CAFF:</h1>
        {comments.length==0 ? <p>No comments yet.</p> : comments}
      </div>
    );
  }
}

class CommentBox extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      comment: ""
    }
  }
  
  handleChange(e) {
    this.setState({comment: e.target.value});
  }

  handleSubmit(e) {
    alert("TODO save comment: " + this.state.comment);
  }

  render() {
    return (
      <div>
        <h2>Add your comment here:</h2>
        <form id="comment-form">
          <textarea rows="5" cols="70" name="comment" onChange={this.handleChange.bind(this)} value={this.state.comment}></textarea>
          <br />
          <button onClick={this.handleSubmit.bind(this)}>Submit Comment</button>
        </form>
      </div>
    );
  }
}

export default withRouter(class CaffPage extends React.Component {
  render() {
    const id = this.props.router.query.id
    const caffFile = new CaffFile(id, "Caff"+id, "user1",
                                  ["cat", "kimchi", "ramen"], "../placeholder.webp");
  
    return (
      <div>
        <ThymeHeader />
        <div className="caff-page-row">
          <div className="column">
            <CaffInfo 
              previewUrl={caffFile.previewUrl}
              tags={caffFile.tags}
              caffId={id}
              router={this.props.router}
            />
          </div>
          <div className="column">
            <CommentList 
              caffId={id}
            />
            <CommentBox></CommentBox>
          </div>
        </div>
      </div>
    );
  }
})