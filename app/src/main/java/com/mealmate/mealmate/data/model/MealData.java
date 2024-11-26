package com.mealmate.mealmate.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class MealData implements Parcelable {

    private String image;
    private Map<String, String> ingredients;
    private String instructions;
    private Long time;
    private String title;
    private String type;
    private String id;

    // Default constructor (required for Firebase Firestore deserialization)
    public MealData() {
    }

    // Parameterized constructor
    public MealData(String image, Map<String, String> ingredients, String instructions, Long time, String title, String type, String id) {
        this.image = image;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.time = time;
        this.title = title;
        this.type = type;
        this.id = id;
    }

    // Parcelable implementation
    protected MealData(Parcel in) {
        image = in.readString();
        ingredients = in.readHashMap(ClassLoader.getSystemClassLoader());
        instructions = in.readString();
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readLong();
        }
        title = in.readString();
        type = in.readString();
        id = in.readString();
    }

    public static final Creator<MealData> CREATOR = new Creator<MealData>() {
        @Override
        public MealData createFromParcel(Parcel in) {
            return new MealData(in);
        }

        @Override
        public MealData[] newArray(int size) {
            return new MealData[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeMap(ingredients);
        dest.writeString(instructions);
        if (time == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(time);
        }
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Map<String, String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<String, String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
