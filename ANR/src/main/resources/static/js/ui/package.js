define([ "./jquerybox", "./jqueryboxsize", //
"./jquerycomputesizemixin", "./jquerytrackingbox", "./tweenlitesyncscreenmixin",//
"./animateappearancecss" //
], //
function(JQueryBox, JQueryBoxSize, //
JQUeryComputeSizeMixin, JQueryTrackingBox, TweenLiteSyncScreenMixin,//
AnimateAppeareanceCss) {
	return {//
	AnimateAppeareanceCss : AnimateAppeareanceCss,//
	JQueryBox : JQueryBox,//
	JQueryBoxSize : JQueryBoxSize,//
	JQUeryComputeSizeMixin : JQUeryComputeSizeMixin,//
	JQueryTrackingBox : JQueryTrackingBox,//
	TweenLiteSyncScreenMixin : TweenLiteSyncScreenMixin // 
	};
});