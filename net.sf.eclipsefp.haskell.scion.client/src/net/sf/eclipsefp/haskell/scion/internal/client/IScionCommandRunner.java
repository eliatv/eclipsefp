package net.sf.eclipsefp.haskell.scion.internal.client;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import net.sf.eclipsefp.haskell.scion.exceptions.ScionCommandException;
import net.sf.eclipsefp.haskell.scion.exceptions.ScionServerException;
import net.sf.eclipsefp.haskell.scion.internal.commands.ScionCommand;

public interface IScionCommandRunner extends ISchedulingRule {
	
	public void runCommandSync(ScionCommand command) throws ScionServerException, ScionCommandException;

}
