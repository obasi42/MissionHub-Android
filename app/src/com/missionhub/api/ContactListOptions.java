//package com.missionhub.api;
//
//import java.util.Collection;
//
//public class ContactListOptions extends ListOptions {
//
//    private ContactListOptions(final ListOptions filters) {
//        super(filters);
//    }
//
//    public static class Builder {
//
//        private final ListOptions filters = new ListOptions();
//
//        public Builder id(final long personId) {
//            filters.addFilter("ids", personId);
//            return this;
//        }
//
//        public Builder id(final Collection<Long> personIds) {
//            filters.addFilter("ids", personIds);
//            return this;
//        }
//
//        public Builder assignedToId(final long personId) {
//            filters.addFilter("assigned_to_id", personId);
//            return this;
//        }
//
//        public Builder personId(final long personId) {
//            filters.addFilter("person_id", personId);
//            return this;
//        }
//
//        public ContactListOptions build() {
//            return new ContactListOptions(filters);
//        }
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
//
//}