package com.missionhub.model.generic;

import com.missionhub.R;
import com.missionhub.util.ResourceUtils;

public enum Gender {
    m, M, male, Male, f, F, female, Female;

    public Gender normalize() {
        switch (this) {
            case m:
            case M:
            case male:
            case Male:
                return male;
            case f:
            case F:
            case female:
            case Female:
                return female;
            default:
                return null;
        }

    }

    @Override
    public String toString() {
        switch (normalize()) {
            case male:
                return ResourceUtils.getString(R.string.gender_male);
            case female:
                return ResourceUtils.getString(R.string.gender_female);
            default:
                return "";
        }
    }

    public String toFilter() {
        switch (normalize()) {
            case male:
                return "m";
            case female:
                return "f";
            default:
                return "";
        }
    }
}