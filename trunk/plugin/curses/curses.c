#include "curses.h"

#define JAVA_CALL(...) ((*env)->CallVoidMethod(env, NativeWrapperObj, __VA_ARGS__))
#define JAVA_CALL_INT(...) ((*env)->CallIntMethod(env, NativeWrapperObj, __VA_ARGS__))
#define JAVA_METHOD(m,s) ((*env)->GetMethodID(env, NativeWrapperClass, m, s))

WINDOW _stdscr = {0};
WINDOW* stdscr = &_stdscr;
WINDOW _newwin = {0};

/* JVM enviroment */
static JavaVM *jvm;
static JNIEnv *env;

static jclass NativeWrapperClass;
static jobject NativeWrapperObj;

/* Java Methods */
static jmethodID NativeWrapper_fatal;
static jmethodID NativeWrapper_warn;
static jmethodID NativeWrapper_addnstr;
static jmethodID NativeWrapper_attrset;
static jmethodID NativeWrapper_overwrite;
static jmethodID NativeWrapper_touchwin;
static jmethodID NativeWrapper_hline;
static jmethodID NativeWrapper_clear;
static jmethodID NativeWrapper_clrtoeol;
static jmethodID NativeWrapper_clrtobot;
static jmethodID NativeWrapper_noise;
static jmethodID NativeWrapper_newwin;
static jmethodID NativeWrapper_refresh;
static jmethodID NativeWrapper_getch;
static jmethodID NativeWrapper_move;
static jmethodID NativeWrapper_curs_set;
static jmethodID NativeWrapper_flushinp;
static jmethodID NativeWrapper_getcury;
static jmethodID NativeWrapper_getcurx;

void (*angdroid_quit_hook)(void) = NULL;

/*
   does not take curses standard attribute pair,
   instead takes an RGB value for text forecolor
*/
int angdroid_attrset(int attrs) {
	JAVA_CALL(NativeWrapper_attrset, attrs);
	return 0;
}

/*
int attrset(int attrs) {
	return 0;
}
*/

int addch(const char a){
	addnstr(1,&a);
	return 0;
}

int addstr(const char *s){
	addnstr(strlen(s), s);
	return 0;
}

int addnstr(int n, const char *s) {
	jbyteArray array = (*env)->NewByteArray(env, n);
	if (array == NULL) angdroid_quit("Error: Out of memory");
	(*env)->SetByteArrayRegion(env, array, 0, n, s);
	JAVA_CALL(NativeWrapper_addnstr, n, array);
	(*env)->DeleteLocalRef(env, array);
	return 0;
}

int move(int y, int x) {
	JAVA_CALL(NativeWrapper_move, y, x);
	return 0;
}

int mvaddch(int y, int x, const char a){
	move(y,x);
	addch(a);
	return 0;
}

int mvaddstr(int y, int x, const char *s){
	move(y,x);
	addstr(s);
	return 0;
}

int hline(const char a, int n){
	JAVA_CALL(NativeWrapper_hline, a, n);
	return 0;
}

int clrtoeol(void){
	JAVA_CALL(NativeWrapper_clrtoeol);
	return 0;
}

int clear(void){
	JAVA_CALL(NativeWrapper_clear);
	return 0;
}

int initscr() {
	return 0;
}

int clrtobot(void){
	JAVA_CALL(NativeWrapper_clrtobot);
	return 0;
}

int getcurx(WINDOW *w){
	return JAVA_CALL_INT(NativeWrapper_getcurx);
}

int getcury(WINDOW *w){
	return JAVA_CALL_INT(NativeWrapper_getcury);
}

int curs_set(int v) {
	JAVA_CALL(NativeWrapper_curs_set, v);
}

WINDOW* newwin(int rows, int cols, 
			   int begin_y, int begin_x) {
	int k = JAVA_CALL_INT(NativeWrapper_newwin, rows,cols,begin_y,begin_x);
	_newwin.w = k; //hack!
	return &_newwin;
}

int overwrite(const WINDOW *src, WINDOW *dst){
	JAVA_CALL(NativeWrapper_overwrite, src->w, dst->w);
	return 0;
}

int touchwin(WINDOW *w){
	JAVA_CALL(NativeWrapper_touchwin, w->w);
	return 0;
}

int refresh(void){
	JAVA_CALL(NativeWrapper_refresh);
	return 0;
}

int angdroid_getch(int v) {
	int k = JAVA_CALL_INT(NativeWrapper_getch, v);
	return k;
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
	NativeWrapper_addnstr = JAVA_METHOD("addnstr", "(I[B)V");
	NativeWrapper_attrset = JAVA_METHOD("attrset", "(I)V");
	NativeWrapper_overwrite = JAVA_METHOD("overwrite", "(II)V");
	NativeWrapper_touchwin = JAVA_METHOD("touchwin", "(I)V");
	NativeWrapper_hline = JAVA_METHOD("hline", "(BI)V");
	NativeWrapper_clrtobot = JAVA_METHOD("clrtobot", "()V");
	NativeWrapper_clrtoeol = JAVA_METHOD("clrtoeol", "()V");
	NativeWrapper_clear = JAVA_METHOD("clear", "()V");
	NativeWrapper_noise = JAVA_METHOD("noise", "()V");
	NativeWrapper_refresh = JAVA_METHOD("refresh", "()V");
	NativeWrapper_getch = JAVA_METHOD("getch", "(I)I");
	NativeWrapper_getcury = JAVA_METHOD("getcury", "()I");
	NativeWrapper_getcurx = JAVA_METHOD("getcurx", "()I");
	NativeWrapper_newwin = JAVA_METHOD("newwin", "(IIII)I");
	NativeWrapper_move = JAVA_METHOD("move", "(II)V");
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



