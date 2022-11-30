import React from 'react';
import ThymeHeader from '../components/header.js';
import Layout from "../components/layout"
import styles from "../styles/signin.module.css"

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
        // TODO check if both are present (html required not working??)
		const formData = new FormData();
        formData.append('caffName', this.state.caffName);
		formData.append('file', this.state.selectedFile);
        alert("TODO upload the caff: " + this.state.caffName + ", filename: " + formData.get("file").name);

        /*
		fetch(
			'https://freeimage.host/api/1/upload?key=<YOUR_API_KEY>',
			{
				method: 'POST',
				body: formData,
			}
		)
			.then((response) => response.json())
			.then((result) => {
				console.log('Success:', result);
			})
			.catch((error) => {
				console.error('Error:', error);
			});
	    };*/
	};
    
    render() {
        return (
            <Layout>
            <div className={styles.container}>
              <h1 className={styles.title}>Upload New CAFF</h1>
              <div className={styles.form}>
                <input className={styles.input} type="text" name="caffname" placeholder="CAFF name" value={this.state.caffName} onChange={this.handleChange.bind(this)} required />
                <input type="file" id="myFile" name="filename" accept=".caff" onChange={this.fileChangeHandler.bind(this)} required />
                <button className={styles.btn} onClick={this.handleSubmit.bind(this)}>Submit</button>
              </div>
            </div>
          </Layout>
        );
    }
}

class UploadPage extends React.Component {
  render() {
    return (
      <div>
        <ThymeHeader />
        <UploadForm />
      </div>
    )
  }
}

// ========================================

export default UploadPage