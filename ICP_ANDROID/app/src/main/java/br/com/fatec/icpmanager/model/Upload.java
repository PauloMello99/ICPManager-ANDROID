package br.com.fatec.icpmanager.model;

public class Upload {

    private String photo;

    public Upload(){  }

    public Upload(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Upload{" +
                "photo='" + photo + '\'' +
                '}';
    }
}
