
// larn includes

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/stat.h>

#include "larncons.h"
#include "larndata.h"
//#include "larnfunc.h"

#include "curses.h"

WINDOW* message;
int msg_cleared = 1;

char SCORENAME[1024];//"larn.scr"
char LOGFNAME[1024];		//"larn.log"
char HELPNAME[1024];		//"larn.hlp"
char LEVELSNAME[1024];	//"larn.maz"
char FORTSNAME[1024];	//"larn.ftn"
char PLAYERIDS[1024];	//"larn.pid"
char DIAGFILE[1024];		//"diagfile"
char SAVEFILE[1024];		//"larn.sav"
char OCOLORNAME[1024];		//"ocolor.txt"
char MCOLORNAME[1024];		//"mcolor.txt"

static char android_files_path[1024];
static char android_savefile[50];

make_filename(char **fname, char *name) {
	*fname = (char*)malloc(1024);
	sprintf(*fname,"%s/save/%s",android_files_path,android_savefile);
}

int file_exists(const char *fname)
{
	struct stat st;
	return (stat(fname, &st) == 0);
}

int angdroid_unlink_save() {
	char savefile[1024];
	sprintf(savefile,"%s/save/%s",android_files_path,android_savefile);
	if (file_exists(savefile)) unlink(savefile);
}

int angdroid_save_flag;
int angdroid_save() {
	int char_saved = FALSE;
	int err = FALSE;

	if (angdroid_save_flag) {

		wclear(message);

		char new_savefile[1024];
		char old_savefile[1024];
		char savefile[1024];
		sprintf(savefile,"%s/save/%s",android_files_path,android_savefile);

		/* New savefile */
		sprintf(new_savefile, "%s.new", savefile);
		sprintf(old_savefile, "%s.old", savefile);

		/* Make sure that the savefile doesn't already exist */
		unlink(new_savefile);
		unlink(old_savefile);

		mvwaddstr(message,0,0,"Saving... ");
		wrefresh(message);

		savegame(new_savefile); //save and continue
		int char_saved = file_exists(new_savefile);

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

					angdroid_save_flag = 0;

					waddstr(message,"done.");
					wrefresh(message);
					LOGD("angdroid_save.save success");
				}
			}

			if(err) {
				waddstr(message,"failed.");
				wrefresh(message);
				LOGD("angdroid_save.save failed.1");
			}

			msg_cleared = 0;
			return err ? FALSE : TRUE;
		}
		else {
			/* Delete temp file */
			unlink(new_savefile);
			waddstr(message,"failed.");
			wrefresh(message);

			LOGD("angdroid_save.save failed.2");
			msg_cleared = 0;
			return FALSE;
		}
	}
	else {
		LOGD("angdroid_save.save skipped");
	}
}

char getch(void){
	//curs_set(1);
	//refresh();
	int i=angdroid_getch(1);
	if (i==-1) {
		angdroid_save();
		while (i==-1) {i = angdroid_getch(1);}
	}
	//curs_set(0);

	if (!msg_cleared) { touchwin(stdscr); refresh(); msg_cleared = 1; }

	return (char)i;
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
	usleep(microsec/1000);

	// if input available, consume & return 1
	int ch = angdroid_getch(0);

	return ch!=0;
}
int kbhit() {
	return check_input(0);
}

/* Find a default user name from the system. */
void user_name(buf, id)
  char *buf;
  int id;
{
  (void) strcpy(buf, android_savefile);
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
	endwin();
}

void initGame(void) {
	angdroid_quit_hook = quit_hook;
	angdroid_save_flag = 0;

	initscr();
	start_color();

	message = newwin(1,20,0,0);
	wattrset(message, COLOR_WHITE);

	attrset(COLOR_WHITE);
	curs_set(1);

	sprintf(SCORENAME,"%s/file/%s",android_files_path,"larn.scr");
	sprintf(LOGFNAME,"%s/file/%s",android_files_path,"larn.log");
	sprintf(HELPNAME,"%s/file/%s",android_files_path,"larn.hlp");
	sprintf(LEVELSNAME,"%s/file/%s",android_files_path,"larn.maz");
	sprintf(FORTSNAME,"%s/file/%s",android_files_path,"larn.ftn");
	sprintf(PLAYERIDS,"%s/file/%s",android_files_path,"larn.pid");
	sprintf(DIAGFILE,"%s/file/%s",android_files_path,"diagfile");
	sprintf(SAVEFILE,"%s/save/%s",android_files_path,android_savefile);

	sprintf(OCOLORNAME,"%s/file/%s",android_files_path,"ocolor.txt");
	sprintf(MCOLORNAME,"%s/file/%s",android_files_path,"mcolor.txt");
}

void angdroid_main() {
	LOGD("angdroid_main()");
	initGame();

	char *a = "larn";
	char *args[2] = {a,NULL}; 

	main(1,args);

	LOGD("angdroid_main exit normally");
	angdroid_quit(NULL);
}

	/* a curses subwindow test display pattern
	initscr();
	start_color();
	WINDOW* MapWindow = newwin(17, 67, 0, 0);
	WINDOW* StatusWindow = newwin(2, 80, 17, 0);
	WINDOW* EffectsWindow = newwin(17, 13, 0, 67);
	WINDOW* MessageWindow = newwin(5, 80, 19, 0);

	int x,y;
	wattrset(MapWindow,COLOR_BLUE);
	for (y = 0 ; y < 17 ; y++) {
		for (x = 0 ; x < 67 ; x++) {
			mvwaddch(MapWindow, y, x, 'X');
		}
	}
	wrefresh(MapWindow);
	wattrset(StatusWindow,COLOR_RED);
	for (y = 0 ; y < 2 ; y++) {
		for (x = 0 ; x < 80 ; x++) {
			mvwaddch(StatusWindow, y, x, 'X');
		}
	}
	wrefresh(StatusWindow);
	wattrset(EffectsWindow,COLOR_GREEN);
	for (y = 0 ; y < 17 ; y++) {
		for (x = 0 ; x < 13 ; x++) {
			mvwaddch(EffectsWindow, y, x, 'X');
		}
	}
	wrefresh(EffectsWindow);
	wattrset(MessageWindow,COLOR_YELLOW);
	for (y = 0 ; y < 5 ; y++) {
		for (x = 0 ; x < 80 ; x++) {
			mvwaddch(MessageWindow, y, x, 'X');
		}
	}
	wrefresh(MessageWindow);

	ularn_getch();
	*/

