/**
 Copyright (c) 2013 Sven Schindler

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

package com.lovbomobile.android.locsy.test.activities;

import com.lovbomobile.android.locsy.client.LocsyClient;
import com.lovbomobile.android.locsy.entities.Location;
import com.lovbomobile.android.locsy.entities.ParceableLocation;

import java.util.HashMap;

public class ClientMock implements LocsyClient {
    @Override
    public HashMap<String, ParceableLocation> getFriendsLocation(String userID, String password, String serverAddress) {
        HashMap<String,ParceableLocation> locations = new HashMap<String, ParceableLocation>();
        ParceableLocation testLocation = new ParceableLocation(0,0);
        locations.put("testPerson",testLocation);
        return locations;
    }

    @Override
    public void sendLocationToServer(Location location, String userID, String password, String serverAddress) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
