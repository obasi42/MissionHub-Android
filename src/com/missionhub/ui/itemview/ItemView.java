/*
 * Copyright (C) 2010 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Modified by C.Roemmich 2012 for MissionHub Project
 */
package com.missionhub.ui.itemview;

import android.view.ViewGroup;

import com.missionhub.ui.ItemAdapter;
import com.missionhub.ui.item.Item;

/**
 * <p>
 * An ItemView defines several methods necessary to the {@link ItemAdapter} in order to process {@link Item}s.
 * </p>
 * <p>
 * When developing your own ItemViews, make sure they all implement this interface.
 * </p>
 * 
 * @author Cyril Mottier
 */
public interface ItemView {

	/**
	 * Called by the {@link ItemAdapter} the first time the ItemView is created. This is usually a good time to keep
	 * references on sub-Views.
	 */
	void prepareItemView();

	/**
	 * Called by the {@link ItemAdapter} whenever an ItemView is displayed on screen. This may occur at the first
	 * display time or when the ItemView is reused by the ListView.
	 * 
	 * @param item
	 *            The {@link Item} containing date used to populate this ItemView
	 * @param parent
	 *            The {@link ViewGroup} that contains the view.
	 * @params position The position of the view in the adapter
	 */
	void setObject(Item item, ViewGroup parent, int position);

	Class<? extends Item> getItemClass();

}