package com.missionhub.model.generic;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.model.Organization;
import com.missionhub.util.ResourceUtils;

import org.apache.commons.lang3.StringUtils;

public enum InteractionVisibility {
    everyone, parents, organization, admins, me;

    private String mTranslatedName;

    public static InteractionVisibility parse(final String string) {
        try {
            return valueOf(string.replace(' ', '_').toLowerCase());
        } catch (Exception e) {
            return everyone;
        }
    }

    @Override
    public String toString() {
        if (mTranslatedName == null) {
            switch (this) {
                case everyone:
                    mTranslatedName = ResourceUtils.getString(R.string.interaction_visibility_everyone);
                    break;
                case parents:
                    Organization parent = Application.getSession().getOrganization().getParent();
                    if (parent != null && StringUtils.isNotEmpty(parent.getName())) {
                        mTranslatedName = String.format(ResourceUtils.getString(R.string.interaction_visibility_everyone_in), parent.getName());
                    } else {
                        mTranslatedName = ResourceUtils.getString(R.string.interaction_visibility_everyone_in_parent);
                    }
                    break;
                case organization:
                    Organization organization = Application.getSession().getOrganization();
                    if (StringUtils.isNotEmpty(organization.getName())) {
                        mTranslatedName = String.format(ResourceUtils.getString(R.string.interaction_visibility_everyone_in), organization.getName());
                    } else {
                        mTranslatedName = ResourceUtils.getString(R.string.interaction_visibility_everyone_in_organization);
                    }
                    break;
                case admins:
                    Organization adminOrg = Application.getSession().getOrganization();
                    if (StringUtils.isNotEmpty(adminOrg.getName())) {
                        mTranslatedName = String.format(ResourceUtils.getString(R.string.interaction_visibility_admins_in), adminOrg.getName());
                    } else {
                        mTranslatedName = ResourceUtils.getString(R.string.interaction_visibility_admins_in_organization);
                    }
                    break;
                case me:
                    mTranslatedName = ResourceUtils.getString(R.string.interaction_visibility_me);
                    break;
            }
            if (StringUtils.isEmpty(mTranslatedName)) {
                mTranslatedName = name();
            }
        }
        return mTranslatedName;
    }
}
