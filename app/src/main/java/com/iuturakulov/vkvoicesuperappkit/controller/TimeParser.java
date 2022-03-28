package com.iuturakulov.vkvoicesuperappkit.controller;

import java.util.Date;

public class TimeParser {

    public String getTimeAgo(long duration) {
        return new Date(duration).toString();
    }

}
