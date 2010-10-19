/* File: borg2.h */
/* Purpose: Header file for "borg2.c" -BEN- */

#ifndef INCLUDED_BORG2_H
#define INCLUDED_BORG2_H

#include "angband.h"

#ifdef ALLOW_BORG

/*
 * This file provides support for "borg2.c".
 */

#include "borg1.h"


/*
 * Determine if a grid is a floor grid
 */
#define borg_cave_floor_bold(Y,X) \
    (!(borg_grids[Y][X].feat & 0x20))


/*
 * Grid based version of "borg_cave_floor_bold()"
 */
#define borg_cave_floor_grid(A) \
    (!((A)->feat & 0x20))




/*
 * Check a path for line of sight
 */
extern bool borg_los(int y1, int x1, int y2, int x2);


/*
 * Check the projection from (x1,y1) to (x2,y2)
 */
extern bool borg_projectable(int y1, int x1, int y2, int x2);
extern bool borg_offset_projectable(int y1, int x1, int y2, int x2);

/*
 * Check the projection from (x1,y1) to (x2,y2).
 */
extern bool borg_projectable_pure(int y1, int x1, int y2, int x2);
extern bool borg_projectable_dark(int y1, int x1, int y2, int x2);


/*
 * Forget the "lite"
 */
extern void borg_forget_lite(void);

/*
 * Update the "lite"
 */
extern void borg_update_lite(void);

/*
 * Forget the "view"
 */
extern void borg_forget_view(void);

/*
 * Update the "view"
 */
extern void borg_update_view(void);


/*
 * Initialize this file
 */
extern void borg_init_2(void);


#endif


#endif

