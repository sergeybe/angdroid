#include "angdroid.h"
#include <jni.h>
#include <android/log.h>
#include <pthread.h>

#define LINES 24
#define COLS 80

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "Angband", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "Angband", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , "Angband", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , "Angband", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "Angband", __VA_ARGS__) 
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG  , "Angband", __VA_ARGS__)

typedef struct WINDOW_s {
	int w;
} WINDOW;
extern WINDOW* stdscr;

#define ERR 1
#define getyx(w, y, x)     (y = getcury(w), x = getcurx(w))

int angdroid_attrset(int);
int addch(const char);
int addstr(const char *);
int addnstr(int, const char *);
int move(int, int);
int mvaddch(int, int, const char);
int mvaddstr(int, int, const char *);
int hline(const char, int);
int clrtobot(void);
int clrtoeol(void);
int clear(void);
int initscr(void);
int curs_set(int);
int getcurx(WINDOW *);
int getcury(WINDOW *);
int overwrite(const WINDOW *, WINDOW *);
int touchwin(WINDOW *);
int refresh(void);

int angdroid_getch(int v);
int flushinp(void);
int noise(void);

void angdroid_quit(const char*);
void angdroid_warn(const char*);

#ifdef USE_MY_STR
size_t my_strcpy(char *, const char *, size_t);
size_t my_strcat(char *, const char *, size_t);
#endif

/* game must implement these */
void angdroid_process_argv(int, const char*);
void angdroid_main(void);
int queryInt(const char* argv0);

