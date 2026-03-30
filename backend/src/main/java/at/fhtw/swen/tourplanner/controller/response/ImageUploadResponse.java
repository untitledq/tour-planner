package at.fhtw.swen.tourplanner.controller.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImageUploadResponse {

    private boolean hasImage;

    public ImageUploadResponse() {
    }

    public ImageUploadResponse(boolean hasImage) {
        this.hasImage = hasImage;
    }

    @Override
    public String toString() {
        return "ImageUploadResponse{" +
                "hasImage=" + hasImage +
                '}';
    }
}