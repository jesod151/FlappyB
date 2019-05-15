package com.jose.main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Box {

    float w, h, x, y;
    Sprite sprite;


    public Box(float _w, float _h, float _x, float _y) {

        w = _w;
        h = _h;
        x = _x;
        y = _y;

    }

    public Box(float _w, float _h, float _x, float _y, Sprite _sprite) {

        w = _w;
        h = _h;
        x = _x;
        y = _y;
        sprite = _sprite;

    }

    public boolean isTouching(Box other){

        if (this.x < other.x + other.w
                && this.x + this.w > other.x
                && this.y < other.y + other.h
                && this.h + this.y > other.y) {
            return true;
        }

        return false;
    }
}
