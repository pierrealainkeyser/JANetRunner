define([ "./jquerybox", //
"./jquerycomputesizemixin", "./jquerytrackingbox", "./tweenlitesyncscreenmixin",//
"./animateappearancecss" //
], //
function(JQueryBox, //
JQUeryComputeSizeMixin, JQueryTrackingBox, TweenLiteSyncScreenMixin,//
AnimateAppeareanceCss) {
	return {//
	AnimateAppeareanceCss : AnimateAppeareanceCss,//
	JQueryBox : JQueryBox,//
	JQUeryComputeSizeMixin : JQUeryComputeSizeMixin,//
	JQueryTrackingBox : JQueryTrackingBox,//
	TweenLiteSyncScreenMixin : TweenLiteSyncScreenMixin // 
	};
});