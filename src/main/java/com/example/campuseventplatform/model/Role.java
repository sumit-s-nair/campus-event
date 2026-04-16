package com.example.campuseventplatform.model;

public enum Role {
    STUDENT {
        @Override
        public boolean canAuthorize(EventStatus targetStatus) {
            return false;
        }
    },
    ORGANIZER {
        @Override
        public boolean canAuthorize(EventStatus targetStatus) {
            return targetStatus == EventStatus.SUBMITTED;
        }
    },
    FACULTY {
        @Override
        public boolean canAuthorize(EventStatus targetStatus) {
            return targetStatus == EventStatus.FACULTY_APPROVED || targetStatus == EventStatus.REJECTED;
        }
    },
    ADMIN {
        @Override
        public boolean canAuthorize(EventStatus targetStatus) {
            return targetStatus == EventStatus.ADMIN_APPROVED
                    || targetStatus == EventStatus.PUBLISHED
                    || targetStatus == EventStatus.REJECTED;
        }
    },
    SPONSOR {
        @Override
        public boolean canAuthorize(EventStatus targetStatus) {
            return false;
        }
    };

    public abstract boolean canAuthorize(EventStatus targetStatus);
}