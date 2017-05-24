angular.module("angular-plugin").directive(
	"menuItem",
	function(PluginMenuService) {
		return {
			transclude: 'element',
			scope: {
				path : "@"
			},
			link: function(scope, el, attr, ctrl, transclude) {
				var items = PluginMenuService.get(scope.path);
				items.forEach(function(each){
					transclude(function(transEl,transScope) {
						transScope.item = each;
						transScope.children = PluginMenuService.get(each.path);
						el.parent().append(transEl);
					});
				});
			}
		}
	});