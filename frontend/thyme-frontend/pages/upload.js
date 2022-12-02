import React from 'react';
import ThymeHeader from '../components/header.js';
import Layout from "../components/layout"
import styles from "../styles/signin.module.css"

import { withRouter } from 'next/router'
import { uploadCaffForm } from '../components/rest-api-calls.js';

class UploadForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            caffName: "",
            selectedFile: undefined
        }
    }

	fileChangeHandler(e) {
		this.setState({selectedFile: e.target.files[0]});
	};

    handleChange(e) {
        this.setState({caffName: e.target.value});
    }

    handleSubmit(e) {
      if(this.state.caffName==="") {
        alert("Please give your CAFF a display name!");
        return;
      }
      if(this.state.selectedFile===undefined) {
        alert("Please choose a .caff file to upload!");
        return;
      }

      const formData = new FormData();
      formData.append('caffName', this.state.caffName);
      formData.append('file', this.state.selectedFile);
      
      uploadCaffForm(formData, this.props.router)
  	};
    
    render() {
        return (
            <Layout>
            <div className={styles.container}>
              <h1 className={styles.title}>Upload New CAFF</h1>
              <div className={styles.form}>
                <input className={styles.input} type="text" name="caffname" placeholder="CAFF name" value={this.state.caffName} onChange={this.handleChange.bind(this)} required />
                <input type="file" id="myFile" name="filename" accept=".caff" onChange={this.fileChangeHandler.bind(this)} required />
                <button type="submit" className={styles.btn} onClick={this.handleSubmit.bind(this)}>Submit</button>
              </div>
            </div>
          </Layout>
        );
    }
}

export default withRouter(class UploadPage extends React.Component {
  render() {
    return (
      <div>
        <ThymeHeader />
        <UploadForm 
          router={this.props.router}
        />
      </div>
    )
  }
})
