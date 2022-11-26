import React from 'react';
import ThymeHeader from '../components/header.js';
import {CaffFile} from '../components/caff-data.js';

function CaffInfo(props) {
  return (
    <div className="caffInfo">
      <h1>{props.caffName}</h1>
      <h2>Added by {props.userName}</h2>
      <p><b>Tags:</b> {props.tags.join(", ")}</p>
      <img className="caffImage" src={props.previewUrl} alt="preview of caff"/>
      <br />
      <button onClick={props.onClick}>Buy CAFF</button>
    </div>
  );
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

class CaffPage extends React.Component {
    render() {
      const caffFile = new CaffFile(1, "Caff1", "user1",
                                    ["cat", "kimchi", "ramen"], "placeholder.webp");

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
              />
            </div>
            <div className="column">
              <CommentList />
            </div>
          </div>
        </div>
      );
    }
  }
  
  // ========================================
  
  export default CaffPage