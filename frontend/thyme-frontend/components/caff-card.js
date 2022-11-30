import React from 'react';
import Link from 'next/link';

function CaffCard(props) {
    return (
      <div className="card" onClick={() => {}}>
        <Link href={"/caff/"+props.caffId}>
          <h2>Name: {props.caffName}</h2>
          <h3>Uploaded by: {props.userName}</h3>
        </Link>
      </div>
    )
}

export default class CaffList extends React.Component {
    render() {
      const cards = this.props.caffArr.map((file) => {
        return (
          <div className="grid-item" key={file.id}>
            <CaffCard 
              caffId={file.id}
              caffName={file.filename}
              userName={file.username}
            />
          </div>
        )
      });
  
      return (
        <div className="grid-container">
          {cards}
        </div>
      );
    }
  }