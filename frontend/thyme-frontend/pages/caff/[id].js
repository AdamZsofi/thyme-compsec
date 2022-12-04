import React from 'react';
import ThymeHeader from '../../components/header.js';
import { withRouter } from 'next/router'
import Link from 'next/link.js';

import { buyCaff, getPreview, uploadComment, checkAdminLoginStatus, getCaff, getComments } from '../../components/rest-api-calls.js';

class CaffInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = { 
      basicCaff: undefined,
      previewImgUrl: undefined,
      isAdmin: false
    }
  }

  async fetchState() {
    this.setState({isAdmin: checkAdminLoginStatus()});

    const caff = await getCaff(this.props.caffId);
    this.setState({basicCaff: caff});

    const previewImgUrl = await getPreview(this.props.caffId);
    this.setState({previewImgUrl: previewImgUrl});
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

  onClick() {
    buyCaff(this.state.basicCaff.id);
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
        <p><b>Tags:</b> {this.state.basicCaff.tags}</p>
        <img className="caffImage" src={this.state.previewImgUrl} alt="preview of caff"/>
        <br />
        <button onClick={this.onClick.bind(this)}>Buy CAFF</button>
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
    uploadComment(this.state.comment, this.props.caffId);
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

    return (
      <div>
        <ThymeHeader />
        <div className="caff-page-row">
          <div className="column">
            <CaffInfo 
              caffId={id}
              router={this.props.router}
            />
          </div>
          <div className="column">
            <CommentList 
              caffId={id}
            />
            <CommentBox 
              caffId={id}
            />
          </div>
        </div>
      </div>
    );
  }
})