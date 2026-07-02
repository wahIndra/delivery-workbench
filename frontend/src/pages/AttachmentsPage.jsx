import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { attachmentApi } from '../api';

export default function AttachmentsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [attachments, setAttachments] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [file, setFile] = useState(null);
  const [category, setCategory] = useState('REQUIREMENT');
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    fetchAttachments();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const fetchAttachments = async () => {
    try {
      const res = await attachmentApi.getList(id);
      setAttachments(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) {
      alert("Please select a file to upload");
      return;
    }
    
    setUploading(true);
    const formData = new FormData();
    formData.append('file', file);
    formData.append('category', category);

    try {
      await attachmentApi.upload(id, formData);
      setFile(null);
      // reset file input
      document.getElementById('file-upload').value = '';
      fetchAttachments();
    } catch (err) {
      alert('Failed to upload attachment');
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async (attachmentId) => {
    if (!window.confirm("Are you sure you want to delete this file?")) return;
    try {
      await attachmentApi.delete(id, attachmentId);
      fetchAttachments();
    } catch (err) {
      alert('Failed to delete attachment');
    }
  };

  const formatBytes = (bytes, decimals = 2) => {
    if (!+bytes) return '0 Bytes';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
  };

  if (loading) return <div className="p-8 text-center">Loading...</div>;

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <h2>Evidence & Attachments</h2>
        <button onClick={() => navigate(`/requests/${id}`)} className="btn btn-secondary">Back to Request</button>
      </div>

      <div className="card mb-8">
        <h3>Upload New Evidence</h3>
        <form onSubmit={handleUpload} className="mt-4 flex flex-col gap-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="form-label">Category *</label>
              <select className="form-control" value={category} onChange={e => setCategory(e.target.value)}>
                <option value="REQUIREMENT">REQUIREMENT</option>
                <option value="PROCESS_FLOW">PROCESS_FLOW</option>
                <option value="TEST_EVIDENCE">TEST_EVIDENCE</option>
                <option value="UAT_SIGNOFF">UAT_SIGNOFF</option>
                <option value="RELEASE_EVIDENCE">RELEASE_EVIDENCE</option>
                <option value="OTHER">OTHER</option>
              </select>
            </div>
            <div>
              <label className="form-label">File *</label>
              <input type="file" id="file-upload" className="form-control" required
                onChange={e => setFile(e.target.files[0])} />
            </div>
          </div>
          <div>
            <button type="submit" className="btn btn-primary" disabled={uploading || !file}>
              {uploading ? 'Uploading...' : 'Upload File'}
            </button>
          </div>
        </form>
      </div>

      <div className="card">
        <h3>Attached Files</h3>
        {attachments.length === 0 ? (
          <p className="text-gray-500 mt-4">No files attached to this request yet.</p>
        ) : (
          <div className="overflow-x-auto mt-4">
            <table className="table w-full text-sm">
              <thead>
                <tr>
                  <th>File Name</th>
                  <th>Category</th>
                  <th>Size</th>
                  <th>Uploaded By</th>
                  <th>Date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {attachments.map(att => (
                  <tr key={att.id}>
                    <td className="font-medium text-gray-900">{att.fileName}</td>
                    <td><span className="badge badge-blue">{att.attachmentCategory}</span></td>
                    <td className="text-gray-500">{formatBytes(att.fileSize)}</td>
                    <td>{att.uploadedBy}</td>
                    <td>{dayjs(att.createdAt).format('MMM D, YYYY HH:mm')}</td>
                    <td>
                      <div className="flex gap-2">
                        <a href={`http://localhost:8080/api/requests/${id}/attachments/${att.id}/download`} 
                           target="_blank" 
                           rel="noreferrer"
                           className="text-primary-600 hover:text-primary-800">
                           Download
                        </a>
                        <button onClick={() => handleDelete(att.id)} className="text-red-600 hover:text-red-800">
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
