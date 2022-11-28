import React from 'react';
import ThymeHeader from '../../components/header.js';
import {CaffFile} from '../../components/caff-data.js';
import { withRouter } from 'next/router'
import Link from 'next/link.js';

async function getCaff(id) {
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

class CaffInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = { basicCaff: {} }
  }

  async componentDidUpdate(prevProps) {
    // Typical usage (don't forget to compare props):
    if (this.props.caffId !== prevProps.caffId) {
        const caff = await getCaff(this.props.caffId);
        this.setState({basicCaff: caff});
    }
  }

  async componentDidMount() {
    const caff = await getCaff(this.props.caffId);
    this.setState({basicCaff: caff});
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
        <h1>Id (Temporary!): {this.state.basicCaff.id}</h1>
        <h1>{this.props.caffName}</h1>
        <h2>Added by {this.props.userName}</h2>
        <p><b>Tags:</b> {this.props.tags.join(", ")}</p>
        <img className="caffImage" src={this.props.previewUrl} alt="preview of caff"/>
        <br />
        <button onClick={this.props.onClick}>Buy CAFF</button>
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

function CommentList(props) {
  return (
    <div>
      <h1>Comments about this CAFF:</h1>
      <Comment 
      userName={"user2"}
      comment={"Nice Cat!"}
      />
    </div>
  );
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
              caffName={caffFile.caffName}
              userName={caffFile.userName}
              previewUrl={caffFile.previewUrl}
              tags={caffFile.tags}
              caffId={id}
            />
          </div>
          <div className="column">
            <CommentList />
          </div>
        </div>
      </div>
    );
  }
})