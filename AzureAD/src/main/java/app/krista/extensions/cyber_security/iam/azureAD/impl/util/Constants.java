/*
 * Azure Ad Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.krista.extensions.cyber_security.iam.azureAD.impl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String STATE = "state";
    public static final String REQUIRED_SCOPE = "openid offline_access User.ReadWrite.All UserAuthenticationMethod.ReadWrite.All";
    public static final String CALLBACK_URI = "/azuread/callback";
    public static final String AUTHENTICATION_METHOD_ID = "28c10230-6103-485e-b985-444c60001490";
    private Constants() {
    }
}
