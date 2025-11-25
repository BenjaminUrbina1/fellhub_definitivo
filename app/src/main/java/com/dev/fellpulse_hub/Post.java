package com.dev.fellpulse_hub;

// Esta es una clase de datos simple (un "molde") para representar una publicación.
public class Post {
    private String userName;
    private String title;
    private String content;
    // En un futuro, aquí podríamos añadir un campo para la imagen, el avatar, etc.
    // private String imageUrl;

    public Post(String userName, String title, String content) {
        this.userName = userName;
        this.title = title;
        this.content = content;
    }

    // Métodos para obtener los datos
    public String getUserName() {
        return userName;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
