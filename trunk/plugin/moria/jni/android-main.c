#include <jni.h>
#include <android/log.h>
#include <pthread.h>
#include <sys/stat.h>

#include "config.h"
#include "constant.h"
#include "types.h"
#include "externs.h"

static char android_files_path[1024];
static char android_savefile[50];
static int turn_save = 0;

char MORIA_SAV			[1024]; //moriasav
char MORIA_TOP			[1024]; //moriatop
char MORIA_MOR			[1024]; //"news"
char MORIA_TOP_NAME 	[1024];	//"scores"
char MORIA_SAV_NAME		[1024]; //"MORIA.SAV"
char MORIA_CNF_NAME		[1024]; //"MORIA.CNF"
char MORIA_HELP			[1024]; //"roglcmds.hlp"
char MORIA_ORIG_HELP 	[1024]; //"origcmds.hlp"
char MORIA_WIZ_HELP		[1024]; //"rwizcmds.hlp"
char MORIA_OWIZ_HELP 	[1024]; //"owizcmds.hlp"
char MORIA_WELCOME		[1024]; //"welcome.hlp"
char MORIA_VER			[1024]; //"version.hlp"

int8u wallsym = '#';
int8u floorsym = '.';

int file_exists(const char *fname)
{
	struct stat st;
	return (stat(fname, &st) == 0);
}

int angdroid_save() {
	int char_saved = FALSE;
	int err = FALSE;
	if (turn_save != turn 
		&& turn > 1
		&& !death) {

		char new_savefile[1024];
		char old_savefile[1024];

		/* New savefile */
		sprintf(new_savefile, "%s.new", savefile);
		sprintf(old_savefile, "%s.old", savefile);

		/* Make sure that the savefile doesn't already exist */
		unlink(new_savefile);
		unlink(old_savefile);

		gotoxy(1,MSG_LINE+1);
		clreol();
		put_buffer("Saving...",0,3);
		char_saved = _save_char(new_savefile,TRUE); //save and continue

		if (char_saved) {

			if (file_exists(savefile) && rename(savefile, old_savefile)!=0)
				err = TRUE;

			if (!err) {
				if (rename(new_savefile, savefile)!=0)
					err = TRUE;

				if (err)
					rename(old_savefile, savefile);
				else {
					unlink(old_savefile);
					turn_save = turn;

					put_buffer(" done.",0,12);
				}
			}

			if(err) {
				put_buffer(" failed.",0,12);
			}

			return err ? FALSE : TRUE;
		} 
		else {

			/* Delete temp file */
			unlink(new_savefile);

			put_buffer(" failed.",0,12);
			return FALSE;
		}
	}
	else {
		LOGD("angdroid_save.save skipped");
	}
}

void textbackground(int c){
}


void textcolor(int c){
	int rgb = 0xFFFFFFFF; //default to white
	switch(c) {
	//case BLACK:
		//break;
	case BLUE:
		rgb = 0xFF0040FF;
		break;
	case GREEN:
		rgb = 0xFF008040;		
		break;
	case CYAN:
		rgb = 0xFF00B0B0; //0xFF00A0A0;
		break;
	case RED:
		rgb = 0xFFC00000;
		break;
	case MAGENTA:
		rgb = 0xFFFF00B0; //0xFFFF00A0;
		break;
	case BROWN: 
		rgb = 0xFFC08040;
		break;
	case LIGHTGRAY:
		rgb = 0xFFC0C0C0;
		break;
	case DARKGRAY: 
		rgb = 0xFF808080;
		break;
	case LIGHTBLUE:
		rgb = 0xFF00EFFF;
		break;
	case LIGHTGREEN: 
		rgb = 0xFF00FF00;
		break;
	case LIGHTCYAN:	
		rgb = 0xFF10FFCC; //0xFF20FFDC;
		break;
	case LIGHTRED: 
		rgb = 0xFFFF5050; //0xFFFF4040;
		break;
	case LIGHTMAGENTA: 
		rgb = 0xFFA898EF; //0xFFB8A8FF;
		break;
	case YELLOW:
		rgb = 0xFFFFFF00;
		break;
	default:
		break;
	}
	angdroid_attrset(rgb);
}

/* Provides for a timeout on input. Does a non-blocking read, consuming the
   data if any, and then returns 1 if data was read, zero otherwise.

   Porting:

   In systems without the select call, but with a sleep for
   fractional numbers of seconds, one could sleep for the time
   and then check for input.

   In systems which can only sleep for whole number of seconds,
   you might sleep by writing a lot of nulls to the terminal, and
   waiting for them to drain, or you might hack a static
   accumulation of times to wait. When the accumulation reaches a
   certain point, sleep for a second. There would need to be a
   way of resetting the count, with a call made for commands like
   run or rest. */
int check_input(int microsec) {
	// sleep for microsec
	usleep(microsec*1000);

	// if input available, consume & return 1
	int ch = angdroid_getch(0);

	return ch!=0;
}

/* Find a default user name from the system. */
void user_name(buf)
  char *buf;
{
  (void) strcpy(buf, "PLAYER");
}

int queryInt(const char* argv0) {
	int result = -1;
	if (strcmp(argv0,"rl")==0) {
		result = 1;
	}
	else {
		result = -1; //unknown command
	}
	return result;
}

void angdroid_process_argv(int i, const char* argv) {
	switch(i){
	case 0: //files path
		my_strcpy(android_files_path, argv, strlen(argv)+1);
		break;
	case 1: //savefile
		my_strcpy(android_savefile, argv, strlen(argv)+1);
		break;
	default:
		break;
	}
}

void quit_hook() {
}

void initGame(void) {
	angdroid_quit_hook = quit_hook;

	strcpy(MORIA_SAV,android_files_path);
	strcpy(MORIA_TOP,android_files_path);
	strcpy(MORIA_MOR,android_files_path);
	strcpy(MORIA_TOP_NAME,android_files_path);
	strcpy(MORIA_SAV_NAME,android_files_path);
	strcpy(MORIA_CNF_NAME,android_files_path);
	strcpy(MORIA_HELP,android_files_path);
	strcpy(MORIA_ORIG_HELP,android_files_path);
	strcpy(MORIA_WIZ_HELP,android_files_path);
	strcpy(MORIA_OWIZ_HELP,android_files_path);
	strcpy(MORIA_WELCOME,android_files_path);
	strcpy(MORIA_VER,android_files_path);

	strcat(MORIA_SAV,"/save/moriasav");
	strcat(MORIA_TOP,"/files/moriatop");
	strcat(MORIA_MOR,"/files/news");
	strcat(MORIA_TOP_NAME,"/files/scores");
	strcat(MORIA_SAV_NAME,"/save/PLAYER");
	strcat(MORIA_CNF_NAME,"/files/moria.cnf");
	strcat(MORIA_HELP,"/files/roglcmds.hlp");
	strcat(MORIA_ORIG_HELP,"/files/origcmds.hlp");
	strcat(MORIA_WIZ_HELP,"/files/rwizcmds.hlp");
	strcat(MORIA_OWIZ_HELP,"/files/owizcmds.hlp");
	strcat(MORIA_WELCOME,"/files/welcome.hlp");
	strcat(MORIA_VER,"/files/version.hlp");
}

void delay(int ms) {
	if (ms) usleep(ms/1000);
}

void angdroid_main() {
	LOGD("angdroid_main()");
	initGame();

	char *a = "moria";
	char *args[2] = {a,NULL}; 

	angdroid_attrset(0xFFFFFFFF);
	curs_set(1);
	LOGD("main()");

	boltdelay = 30;
	main(1,args);

	LOGD("angdroid_main exit normally");
	angdroid_quit(NULL);
}
