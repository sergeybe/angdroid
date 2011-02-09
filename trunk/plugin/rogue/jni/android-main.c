#include <jni.h>
#include <android/log.h>
#include <pthread.h>
#include <sys/stat.h>

#include "rogue.h"
#include "paths.h"
#include "curses.h"

extern boolean msg_cleared;

#define FALSE 0
#define TRUE -1

static char android_files_path[1024];
static char android_savefile[50];
static int turn_save = 0;

char _PATH_SCOREFILE[1024]; //"rogue.sco"
char _PATH_ERRORFILE[1024]; //"rogue.sav"
char _PATH_SCREENFILE[1024]; //"screen.txt"

int color_table[16] = {
	0xFF000000, //BLACK
	0xFF0040FF, //BLUE
	0xFF008040, //GREEN
	0xFF00A0A0, //CYAN
	0xFFFF4040, //RED
	0xFF9020FF, //MAGENTA
	0xFFFFFF00, //YELLOW
	0xFFC0C0C0, //WHITE
	0xFF606060, //GRAY
	0xFF00FFFF, //BRIGHT_BLUE
	0xFF00FF00, //BRIGHT_GREEN
	0xFF20FFDC, //BRIGHT_CYAN
	0xFFFF5050, //BRIGHT_RED
	0xFFB8A8FF, //BRIGHT_MAGENTA
	0xFFFFFF90, //BRIGHT_YELLOW
	0xFFFFFFFF //BRIGHT_WHITE
};

void mvaddcch(short row, short col, color_char cc) {
	move(row, col);
	addcch(cc);
}

addcch(color_char cc)
{
	if (cc.b8.color >= 0 && cc.b8.color < 16)
		angdroid_attrset(color_table[cc.b8.color]);
	addch(cc.b8.ch);
	angdroid_attrset(color_table[7]); //white
}

addcstr(color_char *cstr)
{
	while ((*cstr).b16 != 0) {
		addcch(*cstr++);
	}
}


mvaddcstr(short row, short col, color_char *cstr)
{
	move(row, col);
	addcstr(cstr);
}

addstr_in_color(char *str, byte color)
{
	color_char cc;
	while (*str) {
		cc.b8.color = color;
		cc.b8.ch = *str;
		addcch(cc);
		str++;
	}
}


mvaddstr_in_color(short row, short col, char *str, byte color)
{
	move(row, col);
	addstr_in_color(str, color);
}

void
draw_box(color_char cset[6], int ulrow, int ulcol, int height, int width)
{
	short i;
	const short toprow = ulrow;
	const short bottomrow = ulrow + height - 1;
	const short leftcol = ulcol;
	const short rightcol = ulcol + width - 1;

	/* check for nonsense */
	if (height <= 1 || width <= 1
			|| toprow < 0 || toprow >= DROWS
			|| leftcol < 0 || leftcol >= DCOLS
			|| bottomrow < 0 || bottomrow >= DROWS
			|| rightcol < 0 || rightcol >= DCOLS)
		return;

	/* draw the walls */
	for (i = leftcol + 1; i < rightcol; i++) {
		mvaddcch(toprow, i, cset[0]);
		mvaddcch(bottomrow, i, cset[0]);
	}
	for (i = toprow + 1; i < bottomrow; i++) {
		mvaddcch(i, leftcol, cset[1]);
		mvaddcch(i, rightcol, cset[1]);
	}

	/* add the corners */
	mvaddcch(toprow, leftcol, cset[2]);
	mvaddcch(toprow, rightcol, cset[3]);
	mvaddcch(bottomrow, leftcol, cset[4]);
	mvaddcch(bottomrow, rightcol, cset[5]);
}

//bold (only used in scores)
standout()
{
	//buf_stand_out = 1;
}
//unbold
standend()
{
	//buf_stand_out = 0;
}

colorize(char *str, byte color, color_char *cstr)
{
	while (*str != '\0') {
		(*cstr).b8.color = color;
		(*cstr).b8.ch = *str;
		str++;
		cstr++;
	}
	(*cstr).b16 = 0;
}

color_char mvincch(short row, short col)
{
	color_char foo;
	foo.b8.color = 7; //assume WHITE

	int i;
	int rgb = angdroid_attrget(row,col);
	for(i=0; i<16; i++)
		if (color_table[i] == rgb) foo.b8.color = i;

	foo.b8.ch = mvinch(row,col);
	return foo; 
}

make_filename(char **fname, char *name) {
	*fname = (char*)malloc(1024);
	sprintf(*fname,"%s/save/%s",android_files_path,android_savefile);
}


int file_exists(const char *fname)
{
	struct stat st;
	return (stat(fname, &st) == 0);
}

clear_message()
{
	move(MIN_ROW-1, 0);
	clrtoeol();
	refresh();
	msg_cleared = 1;
}

int angdroid_unlink_save() {
	char savefile[1024];
	sprintf(savefile,"%s/save/%s",android_files_path,android_savefile);
	if (file_exists(savefile)) unlink(savefile);
}

extern int angdroid_save_flag;
int angdroid_save_flag = 0;
int angdroid_save() {
	int char_saved = FALSE;
	int err = FALSE;
	if (angdroid_save_flag) {

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

		clear_message();
		message("Saving...",0);

		save_into_file(new_savefile); //save and continue
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

					clear_message();
					message("Saving... done.",0);
				}
			}

			if(err) {
				message("Saving... failed.",0);
			}

			return err ? FALSE : TRUE;
		}
		else {
			/* Delete temp file */
			unlink(new_savefile);
			message("Saving... failed.",0);

			return FALSE;
		}
	}
	else {
		LOGD("angdroid_save.save skipped");
	}
}

char rgetchar(void){
	curs_set(1);
	refresh();
	int i=angdroid_getch(1);
	if (i==-1) {
		angdroid_save();
		while (i==-1) {i = angdroid_getch(1);}
	}
	curs_set(0);
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
	int i;
	for(i = 0; i<POTIONS; i++) {
		free(id_potions[i].title);
	}
	for(i = 0; i<SCROLS; i++) {
		free(id_scrolls[i].title);
	}
	for(i = 0; i<WANDS; i++) {
		free(id_wands[i].title);
	}
	for(i = 0; i<RINGS; i++) {
		free(id_rings[i].title);
	}
}

void initGame(void) {
	angdroid_quit_hook = quit_hook;

	//take care of rewritable strings
	int i;
	char *ptr;
	for(i = 0; i<POTIONS; i++) {
		ptr = (char*)malloc(strlen(id_potions[i].title)+1);
		strcpy(ptr,id_potions[i].title);
		id_potions[i].title = ptr;
	}
	for(i = 0; i<SCROLS; i++) {
		ptr = (char*)malloc(strlen(id_scrolls[i].title)+1);
		strcpy(ptr,id_scrolls[i].title);
		id_scrolls[i].title = ptr;
	}
	for(i = 0; i<WANDS; i++) {
		ptr = (char*)malloc(strlen(id_wands[i].title)+1);
		strcpy(ptr,id_wands[i].title);
		id_wands[i].title = ptr;
	}
	for(i = 0; i<RINGS; i++) {
		ptr = (char*)malloc(strlen(id_rings[i].title)+1);
		strcpy(ptr,id_rings[i].title);
		id_rings[i].title = ptr;
	}

	strcpy(_PATH_SCOREFILE,android_files_path);
	strcat(_PATH_SCOREFILE,"/save/score");

	strcpy(_PATH_ERRORFILE,android_files_path);
	strcat(_PATH_ERRORFILE,"/save/");
	strcat(_PATH_ERRORFILE,android_savefile);

	strcpy(_PATH_SCREENFILE,android_files_path);
	strcat(_PATH_SCREENFILE,"/save/screen");
}

void angdroid_main() {
	LOGD("angdroid_main()");
	initGame();

	char *a = "rogue";
	char *args[2] = {a,NULL}; 

	angdroid_attrset(color_table[7]); //white
	curs_set(1);
	LOGD("main()");
	main(1,args);

	LOGD("angdroid_main exit normally");
	angdroid_quit(NULL);
}


/* implementations for machdep.c -- see that file for details */
md_sleep(nsecs)
int nsecs;
{
	usleep(nsecs*1000000);
}

char *
md_malloc(n)
int n;
{
	char *t;
	t = (char*)malloc(n);
	return(t);
}

char* md_getenv(char *x) {
	return ((char*)(0));
}

char* md_gln() {return android_savefile;}

boolean
md_df(fname)
char *fname;
{
	if (unlink(fname)) {
		return(0);
	}
	return(1);
}

int
md_get_file_id(fname)
char *fname;
{
    return 0;
}

md_slurp()
{
	flushinp();
}

md_gct(rt_buf)
struct rogue_time *rt_buf;
{
	rt_buf->year = 0;
	rt_buf->month = 0;
	rt_buf->day = 0;
	rt_buf->hour = 0;
	rt_buf->minute = 0;
	rt_buf->second = 0;
}

md_gseed()
{
	return time(NULL);
}


md_gfmt(fname, rt_buf)
char *fname;
struct rogue_time *rt_buf;
{
	return md_gct(rt_buf);
}

