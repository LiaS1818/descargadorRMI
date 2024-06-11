import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class DownloadTask implements Callable<Void> {
    private final String fileUrl;
    private List<String> downloadedFiles = new ArrayList<>(); // Lista para almacenar los nombres de archivo descargados

    DownloadTask(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public Void call() {
        String downloadedFile = Menu.downloadAndUploadFile(fileUrl);
        if (downloadedFile != null) {
            downloadedFiles.add(downloadedFile);
        }
        return null;
    }

    // Getter para acceder a la lista de nombres de archivos descargados
    public List<String> getDownloadedFiles() {
        return downloadedFiles;
    }
}
