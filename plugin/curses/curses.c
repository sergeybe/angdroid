#include "curses.h"

#define JAVA_CALL(...) ((*env)->CallVoidMethod(env, NativeWrapperObj, __VA_ARGS__))
#define JAVA_CALL_INT(...) ((*env)->CallIntMethod(env, NativeWrapperObj, __VA_ARGS__))
#define JAVA_METHOD(m,s) ((*env)->GetMethodID(env, NativeWrapperClass, m, s))

#define WIN_MAX 100
WINDOW _win[WIN_MAX];
WINDOW* stdscr = &_win[0];

/* JVM enviroment */
static JavaVM *jvm;
static JNIEnv *env;

static jclass NativeWrapperClass;
static jobject NativeWrapperObj;

/* Java Methods */
static jmethodID NativeWrapper_fatal;
static jmethodID NativeWrapper_warn;
static jmethodID NativeWrapper_waddnstr;
static jmethodID NativeWrapper_wattrset;
static jmethodID NativeWrapper_wattrget;
static jmethodID NativeWrapper_overwrite;
static jmethodID NativeWrapper_touchwin;
static jmethodID NativeWrapper_whline;
static jmethodID NativeWrapper_wclear;
static jmethodID NativeWrapper_wclrtoeol;
static jmethodID NativeWrapper_wclrtobot;
static jmethodID NativeWrapper_noise;
static jmethodID NativeWrapper_init_color;
static jmethodID NativeWrapper_init_pair;
static jmethodID NativeWrapper_initscr;
static jmethodID NativeWrapper_newwin;
static jmethodID NativeWrapper_delwin;
static jmethodID NativeWrapper_scroll;
static jmethodID NativeWrapper_wrefresh;
static jmethodID NativeWrapper_getch;
static jmethodID NativeWrapper_wmove;
static jmethodID NativeWrapper_mvwinch;
static jmethodID NativeWrapper_curs_set;
static jmethodID NativeWrapper_flushinp;
static jmethodID NativeWrapper_getcury;
static jmethodID NativeWrapper_getcurx;

void (*angdroid_quit_hook)(void) = NULL;

int attrset(int attrs) {
	return wattrset(stdscr, attrs);
}
int wattrset(WINDOW* w, int attrs) {
	JAVA_CALL(NativeWrapper_wattrset, w->w, attrs);
	return 0;
}
int attrget(int row, int col) {
	return wattrget(stdscr,row,col);
}
int wattrget(WINDOW* w,int row, int col) {
	int attrs = JAVA_CALL_INT(NativeWrapper_wattrget, w->w, row, col);
	return attrs;
}

int addch(const char a){
	addnstr(1,&a);
	return 0;
}

int waddch(WINDOW *w, const char a){
	waddnstr(w,1,&a);
	return 0;
}

int addstr(const char *s){
	addnstr(strlen(s), s);
	return 0;
}
int waddstr(WINDOW * w, const char *s){
	waddnstr(w,strlen(s), s);
	return 0;
}

int addnstr(int n, const char *s) {
	waddnstr(stdscr, n, s);
}

int waddnstr(WINDOW* w, int n, const char *s) {
	jbyteArray array = (*env)->NewByteArray(env, n);
	if (array == NULL) angdroid_quit("Error: Out of memory");
	(*env)->SetByteArrayRegion(env, array, 0, n, s);
	JAVA_CALL(NativeWrapper_waddnstr, w->w, n, array);
	(*env)->DeleteLocalRef(env, array);
	return 0;
}

int move(int y, int x) {
	JAVA_CALL(NativeWrapper_wmove, 0, y, x);
	return 0;
}

int wmove(WINDOW* w, int y, int x) {
	JAVA_CALL(NativeWrapper_wmove, w->w, y, x);
	return 0;
}

int mvaddch(int y, int x, const char a){
	move(y,x);
	addch(a);
	return 0;
}

int mvwaddch(WINDOW * w,int y, int x, const char a){
	wmove(w,y,x);
	waddch(w,a);
	return 0;
}

int mvwaddstr(WINDOW* w,int y, int x, const char *s){
	wmove(w,y,x);
	waddstr(w,s);
	return 0;
}
int mvaddstr(int y, int x, const char *s){
	move(y,x);
	addstr(s);
	return 0;
}

int hline(const char a, int n){
	whline(stdscr, a, n);
	return 0;
}
int whline(WINDOW* w, const char a, int n){
	JAVA_CALL(NativeWrapper_whline, w->w, a, n);
	return 0;
}

int clrtoeol(void){
	wclrtoeol(stdscr);
	return 0;
}
int wclrtoeol(WINDOW *w){
	JAVA_CALL(NativeWrapper_wclrtoeol, w->w);
	return 0;
}

int clear(void){
	wclear(stdscr);
	return 0;
}
int wclear(WINDOW* w){
	JAVA_CALL(NativeWrapper_wclear, w->w);
	return 0;
}

int initscr() {
	JAVA_CALL(NativeWrapper_initscr);
	stdscr->w = 0;
	clear();
	touchwin(stdscr);
	return 0;
}

int crmode() {
	return 0;
}
int nonl() {
	return 0;
}
int noecho() {
	return 0;
}
int nl() {
	return 0;
}
int echo() {
	return 0;
}
int endwin() {
	JAVA_CALL(NativeWrapper_initscr);
	return 0;
}
int cbreak() {
	return 0;
}
int nocbreak() {
	return 0;
}
int scrollok(WINDOW *w, int bf){
	return 0;
}
int scroll(WINDOW *w) {
	JAVA_CALL(NativeWrapper_scroll, w->w);
	return 0;
}

int has_colors() {
	return -1;
}
int start_color() {
	int colors = 16;

	int color_table[] = {
		0xFF000000, //BLACK
		0xFF0040FF, //BLUE
		0xFF008040, //GREEN
		0xFF00A0A0, //CYAN
		0xFFFF4040, //RED
		0xFF9020FF, //MAGENTA
		0xFFFFFF00, //YELLOW
		0xFFC0C0C0, //WHITE
		0xFF606060, //BRIGHT_BLACK (GRAY)
		0xFF00FFFF, //BRIGHT_BLUE
		0xFF00FF00, //BRIGHT_GREEN
		0xFF20FFDC, //BRIGHT_CYAN
		0xFFFF5050, //BRIGHT_RED
		0xFFB8A8FF, //BRIGHT_MAGENTA
		0xFFFFFF90, //BRIGHT_YELLOW
		0xFFFFFFFF  //BRIGHT_WHITE
	};

	int i;
	for(i=0; i<colors; i++)
		init_color(i, color_table[i]);
	for(i=0; i<colors; i++)
		init_pair(i, i, 0);

	return 0;
}

int intrflush(WINDOW* w, int bf) {
	return 0;
}
int keypad(WINDOW* w, int bf) {
	return 0;
}
int init_color(int c, int rgb) {
	JAVA_CALL(NativeWrapper_init_color, c, rgb);
	return 0;
} 
int init_pair(int pair, int f, int b){
	JAVA_CALL(NativeWrapper_init_pair, pair, f, b);
	return 0;
} 

int clrtobot(void){
	wclrtobot(stdscr);
	return 0;
}
int wclrtobot(WINDOW* w){
	JAVA_CALL(NativeWrapper_wclrtobot, w->w);
	return 0;
}

int beep() {
	noise();
	return 0;
}

int getcurx(WINDOW *w){
	return JAVA_CALL_INT(NativeWrapper_getcurx, w->w);
}

int getcury(WINDOW *w){
	return JAVA_CALL_INT(NativeWrapper_getcury, w->w);
}

int curs_set(int v) {
	JAVA_CALL(NativeWrapper_curs_set, v);
}

WINDOW* newwin(int rows, int cols, 
			   int begin_y, int begin_x) {
	int k = JAVA_CALL_INT(NativeWrapper_newwin, rows, cols, begin_y, begin_x);

	//hack
	WINDOW* ret = stdscr;
	if (k<WIN_MAX) {
		_win[k].w = k;
		ret = &_win[k];
	}
	return ret;
}
int delwin(WINDOW* w) {
	JAVA_CALL(NativeWrapper_delwin, w->w);
	return 0;
}

int overwrite(const WINDOW *src, WINDOW *dst){
	JAVA_CALL(NativeWrapper_overwrite, src->w, dst->w);
	return 0;
}

int touchwin(WINDOW *w){
	JAVA_CALL(NativeWrapper_touchwin, w->w);
	return 0;
}

int wrefresh(WINDOW *w){
	JAVA_CALL(NativeWrapper_wrefresh, w->w);
	return 0;
}

int refresh(void){
	wrefresh(stdscr);
	return 0;
}

int angdroid_getch(int v) {
	int k = JAVA_CALL_INT(NativeWrapper_getch, v);
	return k;
}

int mvinch(int r, int c) {
	int ch = mvwinch(stdscr,r,c);
	return ch;
}
int mvwinch(WINDOW* w, int r, int c) {
	int ch = JAVA_CALL_INT(NativeWrapper_mvwinch, w->w, r, c);
	return ch;
}

int flushinp() {
	JAVA_CALL(NativeWrapper_flushinp);
	return 0;
}

int noise() {
	JAVA_CALL(NativeWrapper_noise);
	return 0;
}

void angdroid_quit(const char* msg) {
	if (msg) {
		LOGE(msg);
		JAVA_CALL(NativeWrapper_fatal, (*env)->NewStringUTF(env, msg));
	}

	if (angdroid_quit_hook){
		(*angdroid_quit_hook)();
	}

	(*jvm)->DetachCurrentThread(jvm);
	pthread_exit(NULL);
}

void angdroid_warn(const char* msg) {
	if (msg) {
		LOGW(msg);
		JAVA_CALL(NativeWrapper_warn, (*env)->NewStringUTF(env, msg));
	}
}

JNIEXPORT void JNICALL angdroid_gameStart
(JNIEnv *env1, jobject obj1, jint argc, jobjectArray argv)
{
	env = env1;

	if ((*env)->GetJavaVM(env, &jvm) < 0)
		angdroid_quit("Error: Can't get JavaVM!");

	(*jvm)->AttachCurrentThread(jvm, &env, NULL);

	/* Save objects */
	NativeWrapperObj = obj1;

	/* Get NativeWrapper class */
	NativeWrapperClass = (*env)->GetObjectClass(env, NativeWrapperObj);

	/* NativeWrapper Methods */
	NativeWrapper_fatal = JAVA_METHOD("fatal", "(Ljava/lang/String;)V");	
	NativeWrapper_warn = JAVA_METHOD("warn", "(Ljava/lang/String;)V");
	NativeWrapper_waddnstr = JAVA_METHOD("waddnstr", "(II[B)V");
	NativeWrapper_wattrset = JAVA_METHOD("wattrset", "(II)V");
	NativeWrapper_wattrget = JAVA_METHOD("wattrget", "(III)I");
	NativeWrapper_overwrite = JAVA_METHOD("overwrite", "(II)V");
	NativeWrapper_touchwin = JAVA_METHOD("touchwin", "(I)V");
	NativeWrapper_whline = JAVA_METHOD("whline", "(IBI)V");
	NativeWrapper_wclrtobot = JAVA_METHOD("wclrtobot", "(I)V");
	NativeWrapper_wclrtoeol = JAVA_METHOD("wclrtoeol", "(I)V");
	NativeWrapper_wclear = JAVA_METHOD("wclear", "(I)V");
	NativeWrapper_noise = JAVA_METHOD("noise", "()V");
	NativeWrapper_initscr = JAVA_METHOD("initscr", "()V");
	NativeWrapper_wrefresh = JAVA_METHOD("wrefresh", "(I)V");
	NativeWrapper_getch = JAVA_METHOD("getch", "(I)I");
	NativeWrapper_getcury = JAVA_METHOD("getcury", "(I)I");
	NativeWrapper_getcurx = JAVA_METHOD("getcurx", "(I)I");
	NativeWrapper_init_color = JAVA_METHOD("init_color", "(II)V");
	NativeWrapper_init_pair = JAVA_METHOD("init_pair", "(III)V");
	NativeWrapper_newwin = JAVA_METHOD("newwin", "(IIII)I");
	NativeWrapper_delwin = JAVA_METHOD("delwin", "(I)V");
	NativeWrapper_scroll = JAVA_METHOD("scroll", "(I)V");
	NativeWrapper_wmove = JAVA_METHOD("wmove", "(III)V");
	NativeWrapper_mvwinch = JAVA_METHOD("mvwinch", "(III)I");
	NativeWrapper_curs_set = JAVA_METHOD("curs_set", "(I)V");
	NativeWrapper_flushinp = JAVA_METHOD("flushinp", "()V");

	// process argc/argv 
	jstring argv0 = NULL;
	int i;
	for(i = 0; i < argc; i++) {
		argv0 = (*env1)->GetObjectArrayElement(env1, argv, i);
		const char *copy_argv0 = (*env1)->GetStringUTFChars(env1, argv0, 0);

		LOGD("argv%d = %s",i,copy_argv0);
		angdroid_process_argv(i,copy_argv0);

		(*env1)->ReleaseStringUTFChars(env1, argv0, copy_argv0);
	}

	angdroid_main();
}

JNIEXPORT jint JNICALL angdroid_gameQueryInt
	(JNIEnv *env1, jobject obj1, jint argc, jobjectArray argv) {
	jint result = -1; // -1 indicates error

	// process argc/argv 
	jstring argv0 = NULL;
	int i = 0;

	argv0 = (*env1)->GetObjectArrayElement(env1, argv, i);
	const char *copy_argv0 = (*env1)->GetStringUTFChars(env1, argv0, 0);

	result = (jint)queryInt(copy_argv0);

	(*env1)->ReleaseStringUTFChars(env1, argv0, copy_argv0);

	return result;
}


JNIEXPORT jstring JNICALL angdroid_gameQueryString
	(JNIEnv *env1, jobject obj1, jint argc, jobjectArray argv) {
	return (jstring)0; // null indicates error, i.e. not implemented
}

#ifdef USE_MY_STR
/*
 * The my_strcpy() function copies up to 'bufsize'-1 characters from 'src'
 * to 'buf' and NUL-terminates the result.  The 'buf' and 'src' strings may
 * not overlap.
 *
 * my_strcpy() returns strlen(src).  This makes checking for truncation
 * easy.  Example: if (my_strcpy(buf, src, sizeof(buf)) >= sizeof(buf)) ...;
 *
 * This function should be equivalent to the strlcpy() function in BSD.
 */
size_t my_strcpy(char *buf, const char *src, size_t bufsize) {
	size_t len = strlen(src);
	size_t ret = len;

	/* Paranoia */
	if (bufsize == 0) return ret;

	/* Truncate */
	if (len >= bufsize) len = bufsize - 1;

	/* Copy the string and terminate it */
	(void)memcpy(buf, src, len);
	buf[len] = '\0';

	/* Return strlen(src) */
	return ret;
}


/*
 * The my_strcat() tries to append a string to an existing NUL-terminated string.
 * It never writes more characters into the buffer than indicated by 'bufsize' and
 * NUL-terminates the buffer.  The 'buf' and 'src' strings may not overlap.
 *
 * my_strcat() returns strlen(buf) + strlen(src).  This makes checking for
 * truncation easy.  Example:
 * if (my_strcat(buf, src, sizeof(buf)) >= sizeof(buf)) ...;
 *
 * This function should be equivalent to the strlcat() function in BSD.
 */
size_t my_strcat(char *buf, const char *src, size_t bufsize) {
	size_t dlen = strlen(buf);

	/* Is there room left in the buffer? */
	if (dlen < bufsize - 1)
	{
		/* Append as much as possible  */
		return (dlen + my_strcpy(buf + dlen, src, bufsize - dlen));
	}
	else
	{
		/* Return without appending */
		return (dlen + strlen(src));
	}
}
#endif /* USE_MY_STR */



