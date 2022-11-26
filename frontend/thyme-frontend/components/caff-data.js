export class CaffFile {
    constructor(id, caffName, userName,
                tags=[], previewUrl="#") {
        this.id = id;
        this.caffName = caffName;
        this.userName = userName;
        this.tags = tags;
        this.previewUrl = previewUrl;
    }
}