import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.html',
})
export class UploadComponent {
  file!: File;
  result: any = null;

  constructor(private http: HttpClient) {}

  onFileSelect(event: any) {
    this.file = event.target.files[0];
  }

  upload() {
    console.log('Uploading file:', this.file.name);
    const formData = new FormData();
    formData.append('file', this.file);

    this.http
      .post('http://localhost:3000/upload', formData)
      .subscribe(res => (this.result = res));
  }
}
