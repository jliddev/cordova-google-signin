let fs = null;
let path = null;

function getReveresedClientId(plistPath) {
	return new Promise((resolve,reject)=>{
		fs.readFile(plistPath, 'utf8', (err, data) => {
		  if (err) throw err;

		  let idx = data.indexOf('REVERSED_CLIENT_ID')
		  if( idx === -1) {
		  	throw new Error('failed to find REVERSED_CLIENT_ID key, did you forget to add the plist?')
		  }
		  idx = data.indexOf('<string>', idx) + ('<string>'.length);
		  let endIdx = data.indexOf('</string>', idx)

		  let value = data.substring(idx, endIdx);

		  console.log('reversed_client_id: ', value);

		  resolve(value)
		});
	})
}

function updatePluginXml( reversedClientId, xmlPath ){
	return new Promise((resolve,reject)=>{
		fs.readFile(xmlPath, 'utf8', (err, data) => {
		  if (err) throw err;

		  let key = '$REPLACE$'
		  let idx = data.indexOf(key)
		  let endIdx = idx + key.length

		  let first = data.substring(0, idx)
		  let second = data.substring(endIdx)

		  let newData = first + reversedClientId + second;

		  fs.writeFile( xmlPath, newData, err => {
		  	if( err ) throw err;
		  	resolve();
		  })
		});
	})
}

module.exports = function(context) {
    let cwd = context.opts.plugin.pluginInfo.dir
    let projectDir = context.opts.projectRoot


	fs = context.requireCordovaModule('fs')
    path = context.requireCordovaModule('path')

    var Q = context.requireCordovaModule('q');
    var deferral = new Q.defer();

    let plistPath = path.join(cwd, 'src', 'ios', 'GoogleService-Info.plist')
    let xmlPath = path.join(projectDir, 'platforms', 'ios', '')

    console.log(plistPath)

	getReveresedClientId( plistPath )
		.then((reversedClientId)=> updatePluginXml( reversedClientId, xmlPath))
		.then(()=> console.log('update complete'))
		.then(()=> deferral.resolve() )

    return deferral.promise;
}