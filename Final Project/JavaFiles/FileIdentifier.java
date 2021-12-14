
public class FileIdentifier {
    String fileLocation;
    String fileName;
    int id;

    public FileIdentifier(String fileLocation, String fileName, int id) {
        this.fileLocation = fileLocation;
        this.fileName = fileName;
        this.id = id;


    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
