/*
 * Copyright (C) 1997-2001 Id Software, Inc.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/* These are the files we need to add to LWJake2, from ctf/ - flibit
game.h
g_cmds.c
g_combat.c
g_items.c
g_main.c
g_misc.c
g_save.c
g_spawn.c
g_weapon.c
p_client.c
p_hud.c
p_view.c
p_weapon.c
q_shared.h
git clone https://github.com/id-Software/Quake-2.git
*/

package lwjake2.game;

import lwjake2.Defines;
import lwjake2.Globals;
import lwjake2.util.Math3D;

public class CTF {
	public static final String CTF_VERSION = "1.09b";
	public static final int STAT_CTF_TEAM1_PIC = 17;
	public static final int STAT_CTF_TEAM1_CAPS = 18;
	public static final int STAT_CTF_TEAM2_PIC = 19;
	public static final int STAT_CTF_TEAM2_CAPS = 20;
	public static final int STAT_CTF_FLAG_PIC = 21;
	public static final int STAT_CTF_JOINED_TEAM1_PIC = 22;
	public static final int STAT_CTF_JOINED_TEAM2_PIC = 23;
	public static final int STAT_CTF_TEAM1_HEADER = 24;
	public static final int STAT_CTF_TEAM2_HEADER = 25;
	public static final int STAT_CTF_TECH = 26;
	public static final int STAT_CTF_ID_VIEW = 27;
	public static final int STAT_CTF_MATCH = 28;
	public static final int CONFIG_CTF_MATH = Defines.CS_MAXCLIENTS - 1;
	
	public static final int CTF_NOTEAM = 0;
	public static final int CTF_TEAM1 = 1;
	public static final int CTF_TEAM2 = 2;
	
	public static final int CTF_GRAPPLE_STATE_FLY = 0;
	public static final int CTF_GRAPPLE_STATE_PULL = 1;
	public static final int CTF_GRAPPLE_STATE_HANG = 2;
	
	public static final String CTF_TEAM1_SKIN = "ctf_r";
	public static final String CTF_TEAM2_SKIN = "ctf_b";
	
	public static final int DF_CTF_FORCEJOIN = 131072;
	public static final int DF_ARMOR_PROTECT = 262144;
	public static final int DF_CTF_NO_TECH = 524288;
	
	public static final int CTF_CAPTURE_BONUS = 15; // what you get for capture
	public static final int CTF_TEAM_BONUS = 10; // what your team gets for capture
	public static final int CTF_RECOVERY_BONUS = 1; // what you get for recovery
	public static final int CTF_FLAG_BONUS = 0; // what you get for picking up enemy flag
	public static final int CTF_FRAG_CARRIER_BONUS = 2; // what you get for fragging enemy flag carrier
	public static final int CTF_FLAG_RETURN_TIME = 40; // seconds until auto return
	
	public static final int CTF_CARRIER_DANGER_PROTECT_BONUS = 2; // bonus for fraggin someone who has recently hurt your flag carrier
	public static final int CTF_CARRIER_PROTECT_BONUS = 1; // bonus for fraggin someone while either you or your target are near your flag carrier
	public static final int CTF_FLAG_DEFENSE_BONUS = 1; // bonus for fraggin someone while either you or your target are near your flag
	public static final int CTF_RETURN_FLAG_ASSIST_BONUS = 1; // awarded for returning a flag that causes a capture to happen almost immediately
	public static final int CTF_FRAG_CARRIER_ASSIST_BONUS = 2; // award for fragging a flag carrier if a capture happens almost immediately
	
	public static final int CTF_TARGET_PROTECT_RADIUS = 400; // the radius around an object being defended where a target will be worth extra frags
	public static final int CTF_ATTACKER_PROTECT_RADIUS = 400; // the radius around an object being defended where an attacker will get extra frags when making kills
	
	public static final int CTF_CARRIER_DANGER_PROTECT_TIMEOUT = 8;
	public static final int CTF_FRAG_CARRIER_ASSIST_TIMEOUT = 10;
	public static final int CTF_RETURN_FLAG_ASSIST_TIMEOUT = 10;

	public static final int CTF_AUTO_FLAG_RETURN_TIMEOUT = 30; // number of seconds before dropped flag auto-returns

	public static final int CTF_TECH_TIMEOUT = 60; // seconds before techs spawn again

	public static final int CTF_GRAPPLE_SPEED = 650; // speed of grapple in flight
	public static final int CTF_GRAPPLE_PULL_SPEED = 650; // speed player is pulled at
	
	// match_t
	public static final int MATCH_NONE = 0;
	public static final int MATCH_SETUP = 1;
	public static final int MATCH_PREGAME = 2;
	public static final int MATCH_GAME = 3;
	public static final int MATCH_POST = 4;
	
	// elect_t
	public static final int ELECT_NONE = 0;
	public static final int ELECT_MATCH = 1;
	public static final int ELECT_ADMIN = 2;
	public static final int ELECT_MAP = 3;
	
	public static ctfgame_t ctfgame;
	public static cvar_t ctf = new cvar_t();
	public static cvar_t ctf_forcejoin = new cvar_t();

	public static cvar_t competition = new cvar_t();
	public static cvar_t matchlock = new cvar_t();
	public static cvar_t electpercentage = new cvar_t();
	public static cvar_t matchtime = new cvar_t();
	public static cvar_t matchsetuptime = new cvar_t();
	public static cvar_t matchstarttime = new cvar_t();
	public static cvar_t admin_password = new cvar_t();
	public static cvar_t warp_list = new cvar_t();
	
	public static final String ctf_statusbar =
		"yb	-24 " +

		// health
		"xv	0 " +
		"hnum " +
		"xv	50 " +
		"pic 0 " +

		// ammo
		"if 2 " +
		"	xv	100 " +
		"	anum " +
		"	xv	150 " +
		"	pic 2 " +
		"endif " +

		// armor
		"if 4 " +
		"	xv	200 " +
		"	rnum " +
		"	xv	250 " +
		"	pic 4 " +
		"endif " +

		// selected item
		"if 6 " +
		"	xv	296 " +
		"	pic 6 " +
		"endif " +

		"yb	-50 " +

		// picked up item
		"if 7 " +
		"	xv	0 " +
		"	pic 7 " +
		"	xv	26 " +
		"	yb	-42 " +
		"	stat_string 8 " +
		"	yb	-50 " +
		"endif " +

		// timer
		"if 9 " +
		  "xv 246 " +
		  "num 2 10 " +
		  "xv 296 " +
		  "pic 9 " +
		"endif " +

		//  help / weapon icon 
		"if 11 " +
		  "xv 148 " +
		  "pic 11 " +
		"endif " +

		//  frags
		"xr	-50 " +
		"yt 2 " +
		"num 3 14 " +

		//tech
		"yb -129 " +
		"if 26 " +
		  "xr -26 " +
		  "pic 26 " +
		"endif " +

		// red team
		"yb -102 " +
		"if 17 " +
		  "xr -26 " +
		  "pic 17 " +
		"endif " +
		"xr -62 " +
		"num 2 18 " +
		//joined overlay
		"if 22 " +
		  "yb -104 " +
		  "xr -28 " +
		  "pic 22 " +
		"endif " +

		// blue team
		"yb -75 " +
		"if 19 " +
		  "xr -26 " +
		  "pic 19 " +
		"endif " +
		"xr -62 " +
		"num 2 20 " +
		"if 23 " +
		  "yb -77 " +
		  "xr -28 " +
		  "pic 23 " +
		"endif " +

		// have flag graph
		"if 21 " +
		  "yt 26 " +
		  "xr -24 " +
		  "pic 21 " +
		"endif " +

		// id view state
		"if 27 " +
		  "xv 0 " +
		  "yb -58 " +
		  "string \"Viewing\" " +
		  "xv 64 " +
		  "stat_string 27 " +
		"endif " +

		"if 28 " +
		  "xl 0 " +
		  "yb -78 " +
		  "stat_string 28 " +
		"endif "
	;
	
	public static final String tnames[] = {
		"item_tech1", "item_tech2", "item_tech3", "item_tech4",
		null
	};
	
	public static void stuffcmd(edict_t ent, String s) {
		GameBase.gi.WriteByte(11);
		GameBase.gi.WriteString(s);
		GameBase.gi.unicast(ent, true);
	}
	
	/*--------------------------------------------------------------------------*/

	/*
	=================
	findradius

	Returns entities that have origins within a spherical area

	findradius (origin, radius)
	=================
	*/
	public static edict_t[] loc_findradius(edict_t from[], float[] org, float rad) {
		float[]	eorg = new float[3];
		int j;
		
		
		if (from == null)
			from = GameBase.g_edicts;
		for (int currentEdict = 0; currentEdict < GameBase.g_edicts.length; currentEdict++) {
			if (from[currentEdict].inuse)
				continue;
			for (j = 0; j < 3; j++)
				eorg[j] = org[j] - (from[currentEdict].s.origin[j] + (from[currentEdict].mins[j] + from[currentEdict].maxs[j]) * 0.5f);
			if (Math3D.VectorLength(eorg) > rad)
				continue;
			return from;
		}

		return null;
	}

	static void loc_buildboxpoints(float[][] p, float[] org, float[] mins, float[] maxs) {
		Math3D.VectorAdd(org, mins, p[0]);
		Math3D.VectorCopy(p[0], p[1]);
		p[1][0] -= mins[0];
		Math3D.VectorCopy(p[0], p[2]);
		p[2][1] -= mins[1];
		Math3D.VectorCopy(p[0], p[3]);
		p[3][0] -= mins[0];
		p[3][1] -= mins[1];
		Math3D.VectorAdd(org, maxs, p[4]);
		Math3D.VectorCopy(p[4], p[5]);
		p[5][0] -= maxs[0];
		Math3D.VectorCopy(p[0], p[6]);
		p[6][1] -= maxs[1];
		Math3D.VectorCopy(p[0], p[7]);
		p[7][0] -= maxs[0];
		p[7][1] -= maxs[1];
	}

	static boolean loc_CanSee (edict_t targ, edict_t inflictor) {
		trace_t	trace;
		float[][] targpoints = new float[8][3];
		int i;
		float[] viewpoint = new float[3];

		// bmodels need special checking because their origin is 0,0,0
		if (targ.movetype == Defines.MOVETYPE_PUSH)
			return false; // bmodels not supported

		loc_buildboxpoints(targpoints, targ.s.origin, targ.mins, targ.maxs);
		
		Math3D.VectorCopy(inflictor.s.origin, viewpoint);
		viewpoint[2] += inflictor.viewheight;

		for (i = 0; i < 8; i++) {
			trace = GameBase.gi.trace (viewpoint, Globals.vec3_origin, Globals.vec3_origin, targpoints[i], inflictor, Defines.MASK_SOLID);
			if (trace.fraction == 1.0)
				return true;
		}

		return false;
	}

	/*--------------------------------------------------------------------------*/
	
	public static gitem_t flag1_item;
	public static gitem_t flag2_item;
	
	public static void CTFInit() {
		if (flag1_item == null)
			flag1_item = GameItems.FindItemByClassname("item_flag_team1");
		if (flag2_item == null)
			flag2_item = GameItems.FindItemByClassname("item_flag_team2");
		//memset(&ctfgame, 0, sizeof(ctfgame));
		CTFSetupTechSpawn();

		if (competition.value > 1) {
			ctfgame.match = MATCH_SETUP;
			ctfgame.matchtime = GameBase.level.time + matchsetuptime.value * 60;
		}
	}
	
	public static void CTFSpawn() {
		ctf = GameBase.gi.cvar("ctf", "1", Defines.CVAR_SERVERINFO);
		ctf_forcejoin = GameBase.gi.cvar("ctf_forcejoin", "", 0);
		competition = GameBase.gi.cvar("competition", "0", Defines.CVAR_SERVERINFO);
		matchlock = GameBase.gi.cvar("matchlock", "1", Defines.CVAR_SERVERINFO);
		electpercentage = GameBase.gi.cvar("electpercentage", "66", 0);
		matchtime = GameBase.gi.cvar("matchtime", "20", Defines.CVAR_SERVERINFO);
		matchsetuptime = GameBase.gi.cvar("matchsetuptime", "10", 0);
		matchstarttime = GameBase.gi.cvar("matchstarttime", "20", 0);
		admin_password = GameBase.gi.cvar("admin_password", "", 0);
		warp_list = GameBase.gi.cvar("warp_list", "q2ctf1 q2ctf2 q2ctf3 q2ctf4 q2ctf5", 0);
	}

	/*--------------------------------------------------------------------------*/
	
	/*QUAKED info_player_team1 (1 0 0) (-16 -16 -24) (16 16 32)
	potential team1 spawning position for ctf games
	*/
	public static void SP_info_player_team1(edict_t self) {}
	/*QUAKED info_player_team2 (0 0 1) (-16 -16 -24) (16 16 32)
	potential team2 spawning position for ctf games
	*/
	public static void SP_info_player_team2(edict_t self) {}

	/*--------------------------------------------------------------------------*/
	
	public static String CTFTeamName(int team) {
		switch (team) {
		case CTF_TEAM1:
			return "RED";
		case CTF_TEAM2:
			return "BLUE";
		}
		return "UKNOWN";
	}
	
	public static String CTFOtherTeamName(int team) {
		switch (team) {
		case CTF_TEAM1:
			return "BLUE";
		case CTF_TEAM2:
			return "RED";
		}
		return "UKNOWN";
	}
	
	public static int CTFOtherTeam(int team) {
		switch (team) {
		case CTF_TEAM1:
			return CTF_TEAM2;
		case CTF_TEAM2:
			return CTF_TEAM1;
		}
		return -1; // invalid value
	}
	
	/*--------------------------------------------------------------------------*/
	
	public static void CTFAssignSkin(edict_t ent, String s) {
		int playernum = ent.index - GameBase.g_edicts.length - 1;

		switch (ent.client.resp.ctf_team) {
		case CTF_TEAM1:
			GameBase.gi.configstring(Globals.CS_PLAYERSKINS + playernum,
				ent.client.pers.netname + "\\" + s + CTF_TEAM1_SKIN);
			break;
		case CTF_TEAM2:
			GameBase.gi.configstring(Globals.CS_PLAYERSKINS + playernum,
				ent.client.pers.netname + "\\" + s + CTF_TEAM2_SKIN);
			break;
		default:
			GameBase.gi.configstring(Globals.CS_PLAYERSKINS + playernum, 
				ent.client.pers.netname + "\\" + s);
			break;
		}
		// gi.cprintf(ent, PRINT_HIGH, "You have been assigned to %s team.\n", ent->client->pers.netname);
	}
	
	public static void CTFAssignTeam(gclient_t who) {
		edict_t player;
		int i;
		int team1count = 0, team2count = 0;

		who.resp.ctf_state = 0;

		if (((int) GameBase.dmflags.value & DF_CTF_FORCEJOIN) == 0) {
			who.resp.ctf_team = CTF_NOTEAM;
			return;
		}

		for (i = 1; i <= GameBase.maxclients.value; i++) {
			player = GameBase.g_edicts[i];

			if (!player.inuse || player.client == who)
				continue;

			switch (player.client.resp.ctf_team) {
			case CTF_TEAM1:
				team1count++;
				break;
			case CTF_TEAM2:
				team2count++;
			}
		}
		if (team1count < team2count)
			who.resp.ctf_team = CTF_TEAM1;
		else if (team2count < team1count)
			who.resp.ctf_team = CTF_TEAM2;
		else if (Math.random() > 0.5)
			who.resp.ctf_team = CTF_TEAM1;
		else
			who.resp.ctf_team = CTF_TEAM2;
	}
	
	/*
	================
	SelectCTFSpawnPoint

	go to a ctf point, but NOT the two points closest
	to other players
	================
	*/
	public static edict_t SelectCTFSpawnPoint (edict_t ent) {
		EdictIterator spot;
		edict_t	spot1, spot2;
		int	count = 0;
		int	selection;
		float range, range1, range2;
		String cname;

		if (ent.client.resp.ctf_state != 0)
			if (((int) GameBase.dmflags.value & Defines.DF_SPAWN_FARTHEST) == 0)
				return PlayerClient.SelectFarthestDeathmatchSpawnPoint();
			else
				return PlayerClient.SelectRandomDeathmatchSpawnPoint();

		ent.client.resp.ctf_state++;

		switch (ent.client.resp.ctf_team) {
		case CTF_TEAM1:
			cname = "info_player_team1";
			break;
		case CTF_TEAM2:
			cname = "info_player_team2";
			break;
		default:
			return PlayerClient.SelectRandomDeathmatchSpawnPoint();
		}

		spot = null;
		range1 = range2 = 99999;
		spot1 = spot2 = null;

		while ((spot = GameBase.G_Find(spot, GameBase.findByClass, cname)) != null) {
			count++;
			range = PlayerClient.PlayersRangeFromSpot(spot.o);
			if (range < range1)
			{
				range1 = range;
				spot1 = spot.o;
			}
			else if (range < range2)
			{
				range2 = range;
				spot2 = spot.o;
			}
		}

		if (count == 0)
			return PlayerClient.SelectRandomDeathmatchSpawnPoint();

		if (count <= 2)
			spot1 = spot2 = null;
		else
			count -= 2;

		selection = (int) (Math.random() * count);

		spot = null;
		do {
			spot = GameBase.G_Find(spot, GameBase.findByClass, cname);
			if (spot.o == spot1 || spot.o == spot2)
				selection++;
		} while (selection-- != 0);

		return spot.o;
	}
	
	/*------------------------------------------------------------------------*/
	
	/*
	CTFFragBonuses

	Calculate the bonuses for flag defense, flag carrier defense, etc.
	Note that bonuses are not cumaltive.  You get one, they are in importance
	order.
	*/
	public static void CTFFragBonuses(edict_t targ, edict_t inflictor, edict_t attacker) {
		int i;
		edict_t ent;
		gitem_t flag_item, enemy_flag_item;
		int otherteam;
		EdictIterator flag;
		edict_t carrier = null;
		String c;
		float[] v1 = null, v2 = null;

		if (targ.client != null && attacker.client != null) {
			if (attacker.client.resp.ghost != null)
				if (attacker != targ)
					attacker.client.resp.ghost.kills++;
			if (targ.client.resp.ghost != null)
				targ.client.resp.ghost.deaths++;
		}

		// no bonus for fragging yourself
		if (targ.client == null || attacker.client == null || targ == attacker)
			return;

		otherteam = CTFOtherTeam(targ.client.resp.ctf_team);
		if (otherteam < 0)
			return; // whoever died isn't on a team

		// same team, if the flag at base, check to he has the enemy flag
		if (targ.client.resp.ctf_team == CTF_TEAM1) {
			flag_item = flag1_item;
			enemy_flag_item = flag2_item;
		} else {
			flag_item = flag2_item;
			enemy_flag_item = flag1_item;
		}

		// did the attacker frag the flag carrier?
		if (targ.client.pers.inventory[GameItems.ITEM_INDEX(enemy_flag_item)] != 0) {
			attacker.client.resp.ctf_lastfraggedcarrier = GameBase.level.time;
			attacker.client.resp.score += CTF_FRAG_CARRIER_BONUS;
			GameBase.gi.cprintf(attacker, Globals.PRINT_MEDIUM,
				"BONUS: " + CTF_FRAG_CARRIER_BONUS + "points for fragging enemy flag carrier.\n");

			// the target had the flag, clear the hurt carrier
			// field on the other team
			for (i = 1; i <= GameBase.maxclients.value; i++) {
				ent = GameBase.g_edicts[i];
				if (ent.inuse && ent.client.resp.ctf_team == otherteam)
					ent.client.resp.ctf_lasthurtcarrier = 0;
			}
			return;
		}

		if (targ.client.resp.ctf_lasthurtcarrier > 0.0f &&
			GameBase.level.time - targ.client.resp.ctf_lasthurtcarrier < CTF_CARRIER_DANGER_PROTECT_TIMEOUT &&
			attacker.client.pers.inventory[GameItems.ITEM_INDEX(flag_item)] == 0) {
			// attacker is on the same team as the flag carrier and
			// fragged a guy who hurt our flag carrier
			attacker.client.resp.score += CTF_CARRIER_DANGER_PROTECT_BONUS;
			GameBase.gi.bprintf(Globals.PRINT_MEDIUM,
				attacker.client.pers.netname + " defends " +
				CTFTeamName(attacker.client.resp.ctf_team) + "'s flag carrier against an agressive enemy\n"
			);
			if (attacker.client.resp.ghost != null)
				attacker.client.resp.ghost.carrierdef++;
			return;
		}

		// flag and flag carrier area defense bonuses

		// we have to find the flag and carrier entities

		// find the flag
		switch (attacker.client.resp.ctf_team) {
		case CTF_TEAM1:
			c = "item_flag_team1";
			break;
		case CTF_TEAM2:
			c = "item_flag_team2";
			break;
		default:
			return;
		}

		flag = null;
		while ((flag = GameBase.G_Find(flag, GameBase.findByClass, c)) != null) {
			if ((flag.o.spawnflags & Defines.DROPPED_ITEM) == 0)
				break;
		}

		if (flag == null)
			return; // can't find attacker's flag

		// find attacker's team's flag carrier
		for (i = 1; i <= GameBase.maxclients.value; i++) {
			carrier = GameBase.g_edicts[i];
			if (carrier.inuse && 
				carrier.client.pers.inventory[GameItems.ITEM_INDEX(flag_item)] != 0)
				break;
			carrier = null;
		}

		// ok we have the attackers flag and a pointer to the carrier

		// check to see if we are defending the base's flag
		Math3D.VectorSubtract(targ.s.origin, flag.o.s.origin, v1);
		Math3D.VectorSubtract(attacker.s.origin, flag.o.s.origin, v2);

		if ((Math3D.VectorLength(v1) < CTF_TARGET_PROTECT_RADIUS ||
				Math3D.VectorLength(v2) < CTF_TARGET_PROTECT_RADIUS ||
				loc_CanSee(flag.o, targ) || loc_CanSee(flag.o, attacker)) &&
				attacker.client.resp.ctf_team != targ.client.resp.ctf_team) {
			// we defended the base flag
			attacker.client.resp.score += CTF_FLAG_DEFENSE_BONUS;
			if (flag.o.solid == Defines.SOLID_NOT)
				GameBase.gi.bprintf(Globals.PRINT_MEDIUM,
					attacker.client.pers.netname + " defends the " +
					CTFTeamName(attacker.client.resp.ctf_team) + " base.\n"
				);
			else
				GameBase.gi.bprintf(Globals.PRINT_MEDIUM,
					attacker.client.pers.netname + " defends the " +
					CTFTeamName(attacker.client.resp.ctf_team) + " flag.\n" 
				);
			if (attacker.client.resp.ghost != null)
				attacker.client.resp.ghost.basedef++;
			return;
		}

		if (carrier != null && carrier != attacker) {
			Math3D.VectorSubtract(targ.s.origin, carrier.s.origin, v1);
			Math3D.VectorSubtract(attacker.s.origin, carrier.s.origin, v1);

			if (Math3D.VectorLength(v1) < CTF_ATTACKER_PROTECT_RADIUS ||
					Math3D.VectorLength(v2) < CTF_ATTACKER_PROTECT_RADIUS ||
				loc_CanSee(carrier, targ) || loc_CanSee(carrier, attacker)) {
				attacker.client.resp.score += CTF_CARRIER_PROTECT_BONUS;
				GameBase.gi.bprintf(Globals.PRINT_MEDIUM,
					attacker.client.pers.netname + " defends the " + 
					CTFTeamName(attacker.client.resp.ctf_team) + "'s flag carrier.\n"
				);
				if (attacker.client.resp.ghost != null)
					attacker.client.resp.ghost.carrierdef++;
				return;
			}
		}
	}

	public static void CTFCheckHurtCarrier(edict_t targ, edict_t attacker) {
		gitem_t flag_item;

		if (targ.client == null || attacker.client == null)
			return;

		if (targ.client.resp.ctf_team == CTF_TEAM1)
			flag_item = flag2_item;
		else
			flag_item = flag1_item;

		if (targ.client.pers.inventory[GameItems.ITEM_INDEX(flag_item)] != 0 &&
			targ.client.resp.ctf_team != attacker.client.resp.ctf_team)
			attacker.client.resp.ctf_lasthurtcarrier = GameBase.level.time;
	}
	
	/*------------------------------------------------------------------------*/
	
	public static boolean CTFPickup_Flag(edict_t ent, edict_t other);
	public static boolean CTFDrop_Flag(edict_t ent, gitem_t item);
	public static void CTFEffects(edict_t player);
	public static void CTFCalcScores();
	public static void SetCTFStats(edict_t ent);
	public static void CTFDeadDropFlag(edict_t self);
	public static void CTFScoreboardMessage (edict_t ent, edict_t killer);
	public static void CTFTeam_f (edict_t ent);
	public static void CTFID_f (edict_t ent);
	public static void CTFSay_Team(edict_t who, String msg);
	public static void CTFFlagSetup (edict_t ent);
	public static void CTFResetFlag(int ctf_team);

	// GRAPPLE
	public static void CTFWeapon_Grapple (edict_t ent);
	public static void CTFPlayerResetGrapple(edict_t ent);
	public static void CTFGrapplePull(edict_t self);
	public static void CTFResetGrapple(edict_t self);

	//TECH
	public static gitem_t CTFWhat_Tech(edict_t ent);
	public static boolean CTFPickup_Tech (edict_t ent, edict_t other);
	public static void CTFDrop_Tech(edict_t ent, gitem_t item);
	public static void CTFDeadDropTech(edict_t ent);
	public static void CTFSetupTechSpawn();
	public static int CTFApplyResistance(edict_t ent, int dmg);
	public static int CTFApplyStrength(edict_t ent, int dmg);
	public static boolean CTFApplyStrengthSound(edict_t ent);
	public static boolean CTFApplyHaste(edict_t ent);
	public static void CTFApplyHasteSound(edict_t ent);
	public static void CTFApplyRegeneration(edict_t ent);
	public static boolean CTFHasRegeneration(edict_t ent);
	public static void CTFRespawnTech(edict_t ent);
	public static void CTFResetTech();

	public static void CTFOpenJoinMenu(edict_t ent);
	public static boolean CTFStartClient(edict_t ent);
	public static void CTFVoteYes(edict_t ent);
	public static void CTFVoteNo(edict_t ent);
	public static void CTFReady(edict_t ent);
	public static void CTFNotReady(edict_t ent);
	public static boolean CTFNextMap();
	public static boolean CTFMatchSetup();
	public static boolean CTFMatchOn();
	public static void CTFGhost(edict_t ent);
	public static void CTFAdmin(edict_t ent);
	public static boolean CTFInMatch();
	public static void CTFStats(edict_t ent);
	public static void CTFWarp(edict_t ent);
	public static void CTFBoot(edict_t ent);
	public static void CTFPlayerList(edict_t ent);

	public static boolean CTFCheckRules();

	public static void SP_misc_ctf_banner (edict_t ent);
	public static void SP_misc_ctf_small_banner (edict_t ent);

	public static void UpdateChaseCam(edict_t ent);
	public static void ChaseNext(edict_t ent);
	public static void ChasePrev(edict_t ent);

	public static void CTFObserver(edict_t ent);

	public static void SP_trigger_teleport (edict_t ent);
	public static void SP_info_teleport_destination (edict_t ent);
	
}